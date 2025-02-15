# Docker 명령어 모음

#### 1. docker image 다운로드
> docker pull 'image name' <br>
> ex) **docker pull mysql**

#### 2. docker compose 관련
✅ compose 기동 방법
> docker compose up -d  // docker container 올림<br>
> docker compose down // docker container 내리기<br>

down 은 docker container 를 내림으로써 기존에 모든 정보를 삭제하기 때문에 유의해야 한다 <br>

✅ compose 실행 후 관리
> docker compose start <br> 시작
> docker compose stop <br> // 중지 
> docker compose restart <br> // 재시작


#### 3. docker container 조회
> docker compose ps <br>
> docker ps -a <br>



#### 4. docker 로그
> docker compose logs -f "컨테이너 이름"


#### 5. 

#### 4. docker Option


#### 5. docker 리소스 한번에 정리
별로 알고싶지 않았는데.. 알게 된 명령어
>  docker system prune -a

사용중이지 않은 모든 docker image 및 container 를 한번에 삭제한다 <br>



<br>

> Docker 공식 문서 -> https://docs.docker.com/reference/cli/docker/