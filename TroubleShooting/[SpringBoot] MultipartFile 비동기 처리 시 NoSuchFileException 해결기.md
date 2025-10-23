# Spring Boot MultipartFile 비동기 처리 시 NoSuchFileException 해결기

## 개요
실무에서 파일 I/O 관련 성능 개선을 하던 중 트러블 슈팅 해결방법을 글로 정리하며 머리에 되새겨 보려고 한다 <br>


## 본론
나의 상황은 아래와 같다
```java
@RestController
@RequiredArgsConstructor
public class MultiPartFileController {
    private final MultiPartFileFacade multiPartFileFacade;

    @PostMapping(value = "/api/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @RequestPart(value = "individualFiles", required = true) List<MultipartFile> individualFiles,
            @RequestPart(value = "corporateFiles", required = false) List<MultipartFile> corporateFiles,
            Map<String, String> requestDto
            ) {

        multiPartFileFacade.processFileUpload(individualFiles, corporateFiles, requestDto);
        
        
        return ResponseEntity.ok().build();
    }
}

```

파일 upload 를 처리하는 endpoint 가 존재한다 <br>

위 파일에서 individualFiles 은 최소 3개 ~ 5개 파일이 업로드가 되고 <br>
corporateFiles 은 고정적으로 3개가 업로드가 된다 <br>

그리고 현재는 간편하게 Map 으로 처리하였지만, 실무에서는 실제 요청 DTO 를 정의해서 사용하였다 <br>

위 endpoint 가 성공적으로 처리되는 과정은 아래와 같다.
1. dto validation 체크 후 save
2. individualFiles 파일서버 upload
3. corporateFiles 가 존재할 경우 파일서버 upload

위 플로우는 동기적 흐름으로 동작을 했었다. <br>

위 플로우는 안정적이지만 치명적인 단점이 존재했다. 바로 user 가 파일 upload 가 정상 처리될 때 까지 기다려야 한다는 것이다 <br>
짧게는 3초, 길게는 6초 까지도 기다려야 했다 <br>
(파일들 크기 제한은 10MB 이고 파일 합산 최대는 100MB 로 제한 정책을 해둔 상태긴 하다) <br>


위 문제를 개선하기 위해서 생각해본 방법은 2가지 이다
- 파일 upload 비동기 처리
- future 인터페이스를 활용한 파일 upload 병렬 처리

위 두가지 방법을 생각해 보았고 결론적으로는 Spring Boot 에서는 @Async 를 활용한 비동기 처리가 간편하게 제공되기에 비동기 방식을 채택하였다 <br>

그리고 파일 upload 메소드에 @Async 를 걸어두면 간단하게 끝날 것이라고 생각했는데 아래와 같은 Exception 을 만나게 되었다 <br>
기존 로직은 아래와 같다
```java
@Component
@RequiredArgsConstructor
public class MultiPartFileFacade {
    private final FileUploadService fileUploadService;
    private final FileUseCase fileUseCase;

    @Transactional
    public void processFileUpload(List<MultipartFile> individualFiles, List<MultipartFile> corporateFiles, Map<String, String> requestDto) {

        //1. dto validation 체크 후 dto save 및 file 관련 데이터 db save
        if(fileUseCase.saveFileData(requestDto, individualFiles, corporateFiles)) {
            //2. individualFiles 파일서버 업로드 -> multipartFile 다른 '스레드'로 전달
            fileUploadService.uploadFileList(individualFiles);

            //3. corporateFiles 가 존재할 경우 파일서버 upload -> multipartFile 다른 '스레드'로 전달
            if(corporateFiles.size() != 0)
                fileUploadService.uploadFileList(corporateFiles);
        }
    }

}
```
```java
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final static int PERMISSION = 0_770;
    private static final String UNIX_SEPARATOR = "/";

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void uploadFileList(List<MultipartFile> multipartFileList) throws Exception {
        // 파일 업로드 처리 로직
        
        // 다른 스레드에서 로직 실행

        sftp.put(document.getInputStream(), fileName, ChannelSftp.OVERWRITE);
        // 이미 삭제된 임시 파일에 접근 시도 → NoSuchFileException
    }

}

```

위 로직을 사용한 후에 Test 를 해보니 아래와 같은 결과가 console 에 출력되었다 <br>
```java
[ERROR] 2025-09-25 17:56:31,580 [file-io-1] - UncaughtExceptionHandler | o.s.a.i.SimpleAsyncUncaughtExceptionHandler:39 | []  - [()] : 
Unexpected exception occurred invoking async method: public void {패키지 경로}.FileUploadService.uploadFileList(dto) throws java.lang.Exception java.nio.file.NoSuchFileException: C:\Users\{UserName}\AppData\Local\Temp\tomcat.9910.14172838937199525207\work\Tomcat\localhost\ROOT\upload_a0a4c24a_8293_4b0f_83f4_5bda27677c37_00000027.tmp
```

일단은 결론은 아래와 같다 <br>
파일 업로드 비동기 처리를 위해 로직을 처리하던 중 파일을 처리할 경로에 해당하는 파일이 없다는 것이였다 <br>

왜 그런지 이유를 생각해 보았고, 위 문제를 해결해 나간 내용은 아래와 같다. <br>


### SpringBoot Tomcat MultipartFile 동작 방식
먼저 SpringBoot 에서 MultipartFile 을 어떤식으로 처리하는지 부터 이해를 해야한다 <br>

SpringBoot 내장 톰캣에서 MultipartFile 동작방식은 아래와 같다.
```markdown
HTTP 요청 → Tomcat 임시 파일 생성
(C:\Users\{name}\AppData\Local\Temp\tomcat.xxx\upload_xxx.tmp)
↓
HTTP 응답 완료 → 임시 파일 자동 삭제
↓
비동기 스레드에서 접근 시도 → 파일 없음 (NoSuchFileException)
```

위와 같은 흐름으로 가기 때문에 문제를 해결하는 방법은 명확했다 <br>
임시 파일로 저장이 되지 않게 실제로 저장을 하거나, byte 배열로 변환을 통해 다른 스레드로 전달을 시키는 것이였다 <br>

### 해결 방법
실제로 특정 경로에 파일 저장을 시키고, 업로드가 끝나면 임시 파일을 삭제할까 생각을 했지만, 굳이 서버에 여러 파일들을 업로드하고 삭제하며 cpu 를 사용하는 건 별로라고 생각하여 <br>
byte 배열로 변환하여 다른 스레드에 전달하는 방향으로 문제를 풀어나갔다 <br>

#### 1. byte 배열 변환 후 사용
byte 배열로 파일 데이터를 전송하면 실제 파일 데이터만 전달이 되고, 파일 관련 정보들을 전달이 되지 않아, 위 데이터를 DTO 에 담아서 전달 하는 방식을 사용하였다 <br>


```java
public record FileData(
        String originalFilename,
        String contentType,
        long size,
        byte[] content
) {
    
    public static List<FileData> from(List<MultipartFile> files) throws IOException {
        List<FileData> fileDataList = new ArrayList<>();

        for (MultipartFile file : files) {
            FileData fileData = new FileData(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    file.getBytes()
            );

            fileDataList.add(fileData);
        }

        return fileDataList;
    }
}
```

파일 관련 데이터를 담을 DTO 를 정의하였고, 아래 로직은 실제 데이터를 처리하는 비즈니스 로직이다 <br>

위 부분에서 나중에 고려할 부분은 파일 크기를 체크해서 파일 사이즈가 큰 것은 byte 배열로 변환하지 않고, 임시 경로에 upload 를 하여 처리할 생각은 하고있다 <br>
하지만 현재는 위 부분까지는 고려하지 않고 설계를 하였다 <br>

```java
// 파일 관련 데이터 dto 로 만드는 과정
List<FileData> fileDataList = FileData.from(documents);
fileUploadService.uploadDocuments(fileDataList);
```
```java
// 실제 비즈니스 로직
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {
    
    @Async(value = "fileTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<UploadResult> uploadDocuments(List<FileData> fileDataList) throws Exception {
        // 예외 처리 추가된 로직 처리 패턴
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 업로드 로직
                fileUploadService.uploadFileList(fileDataList);
                
                return UploadResult.success(uploadedFiles);
            } catch (Exception e) {
                log.error("파일 업로드 실패: {}", e.getMessage(), e);
                // 실패 시 정리 작업
                cleanupPartialUploads(fileDataList);
                return UploadResult.failure(e.getMessage());
            }
        });
    }
}
```

실제 비즈니스 로직에서는 비동기 스레드로 파일을 보낸다는 것은 기본적인 파일에 대한 검증(크기, 확장자, 등등) 이 진행되어야 한다 <br>

추가적으로 비동기로 파일을 처리할 때 파일이 유실 되지 않게 꼭 유의를 해야한다 <br>
파일 유실이 되지않게 하는 방법은 여러가지 이므로 이 부분은 따로 설명하지 않겠다 <br>

### 파일 I/O 관련 스레드 풀 성능 최적화
추가적으로 파일 I/O 처럼 시간 소모가 필요한 로직 비동기 처리를 위해서는 전용 스레드 풀을 만들어서 사용하는 것을 추천한다 <br> 

아래는 2core 4gb 기준으로 설계한 스레드 풀이다 <br>
참고로 표준으로 나와있는 스레드 풀 설계 공식 또한 존재한다 <br>
ex) → CPU 코어 수 × (1 + I/O 대기시간 / CPU 사용시간)


```java
@EnableAsync
@Configuration
public class ThreadPoolConfig {
    
    @Bean(name = "fileTaskExecutor")
    public ThreadPoolTaskExecutor fileUploadAsyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("file-io-");

        /*
         * = 2core 4GB 환경 최적화 =
         * CorePoolSize 계산 근거:
         * - CPU 코어 수: 2개
         * - I/O 대기 시간: 평균 2초 (SFTP 업로드)
         * - CPU 사용 시간: 평균 0.1초 (파일 변환)
         * - 공식: 2 * (1 + 2/0.1) = 2 * 21 = 42
         * - 메모리 제약으로 6개로 보수적 설정
         */
        executor.setCorePoolSize(6);       // 안정적인 기본 스레드
        executor.setMaxPoolSize(12);       // 메모리 안전 범위 내 최대
        executor.setQueueCapacity(80);     // 적절한 버퍼링
        executor.setKeepAliveSeconds(120); // 빠른 정리

        // 메모리 부족 시 호출 스레드에서 실행
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}
```


위 설정은 최소한의 설정이고, 각자 상황에 맞게 설정을 하면 더 좋은 결과를 얻을 수 있을 것이다 <br>

위 스레드 풀은 os 레벨에서 스레드는 고려하지 않고 jvm 내부 스레드만 고려한 부분이다 <br>
```java
// JVM 내부 스레드 관리
public class CorrectThreadPoolDesign {

    /**
     * -> 고려해야 할 스레드들 (JVM 내부)
     * 1. Tomcat HTTP 스레드: ~200개 (설정 가능)
     * 3. 커스텀 파일 I/O 스레드: ~12개 (fileTaskExecutor)
     * 4. Spring 스케줄러: ~1개 (기본)
     * 5. JVM 시스템 스레드: ~20개 (GC, Compiler 등)
     * 총합: ~235개 (2core 환경에서 적절)
     */
    
    /** -> 고려할 필요 없는 것들
     * 1. OS 시스템 데몬 (sshd, systemd 등) - JVM과 별개 프로세스
     * 2. 다른 애플리케이션 스레드 - 프로세스 격리
     * 3. 커널 스레드 - OS가 관리
     */
}

```

## 결론
위 문제를 해결해가면서 업로드 시간을 개선하게 되었다 <br>
동일 조건 100회 테스트 결과
- 기존 동기 처리: 평균 3.6초
- 비동기 처리 적용 후: 평균 1.1초 (66% 성능 향상)"


위 문제를 해결해가면서 tomcat 동작 방식 및 os 지식 및 springboot 관련 지식또한 늘게 되어서 기분이 좋았다 <br>

하지만 파일 I/O 를 비동기 처리를 위해서는 아래와 같은 사항이 충분히 고려되어야 한다
- 파일 업로드 실패 시 사용자에게 피드백을 제공 -> 비동기 처리이므로 실시간 오류 알림이나 상태 확인 API가 필요할 것으로 판단
  - 위 부분은 필자는 모니터링 
- 현재 SFTP 업로드가 실패했을 때 재시도 로직이나 파일 무결성 검증

최소한 위 2가지 부분을 고려하여 비동기 로직을 설계한다면 좋을 것 같다. <br>

> 추후 개선 계획
> 1. 추후에는 파일 크기를 확인하여 파일 크기가 큰 경우는 byte 배열로 변환하지 않고 임시 경로에 파일을 저장해두고 처리를 하려고 한다!
 
위 부분을 간단하게 설명하자면 기존 나의 생각은 아래와 같았다 <br>
i/o 를 최소화 하기 위해서 모든 파일을 byte 배열로 바꿔서 처리하면 더 좋지 않을까? 
```java
// byte 배열 변환 -> I/O 비용 절약
MultipartFile file = request.getFile();
byte[] fileBytes = file.getBytes(); // 1번의 I/O만 발생

// 임시 파일 저장 방식
file.transferTo(tempFile);          // 디스크 쓰기 I/O
// 
// 비동기 처리
Files.readAllBytes(tempFile);       // 디스크 읽기 I/O
Files.delete(tempFile);             // 디스크 삭제 I/O
```

하지만 byte 배열을 사용한 방법은 결론만 말하자면 아래와 같은 점이 좋다
- 디스크 I/O 2-3회 절약: 쓰기 → 읽기 → 삭제 vs 메모리 직접 접근
- 파일 시스템 오버헤드 없음: 파일 시스템 메타데이터 업데이트 생략
- OS 버퍼 캐시 부담 감소: 임시 파일이 페이지 캐시를 오염시키지 않음

하지만 단점은 '**메모리 사용량 급증 위험**' 이 있다 <br>
혹시라도 동시간대 요청이 몰리게 되면 '**OOM**' 이 발생할 수 있는 가능성이 생긴다 <br>

최대한 안전한 방법을 선택해야 하기에, 추후 모니터링을 진행하며 하이브리드 방법으로 다시 설계를 진행하려고 한다 



### Reference
> 1. https://www.youtube.com/watch?v=HIGc93pqTAc&t=796s <br>