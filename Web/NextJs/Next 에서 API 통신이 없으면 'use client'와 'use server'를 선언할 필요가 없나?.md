# Next 에서 API 통신이 없으면 'use client'와 'use server'를 선언할 필요가 없나?

기본적으로 Next.js 에선 SSR, CSR 방식을 둘다 지원을 한다. 그리고 요즘 2024 추세는 SSR 을 더 권장하는 추세라고 생각이 된다 <br>
Next 는 리액트 기반의 프레임워크기 때문에 모든 부분을 컴포넌트로 쪼개서 각자 동작을 시키는 쪽으로 코드를 작성한다 <br>

문득 궁금해졌다. <br>
만약 Next.js 에서 백엔드와 통신을 하지않고 그냥 컴포넌트만 그린다면? 'use client', 'use server' 를 선언할 필요가없을까? 라는 고민을 해보았다 <br>
위 고민은 아마 내가 프론트 전문 개발자가 아니라 기초부터 쌓지 않았기 때문에 하는 고민일 수도 있다 <br>

결론부터 말하자면 'use client' 와 'use server' 는 API 통신 여부와는 무관하게 컴포넌트의 실행 환경(클라이언트 또는 서버)을 명시하는 데 사용한다 <br>
즉 직접적인 API 통신이 없더라도, 컴포넌트의 동작 방식에 따라 선언을 해야한다. 컴포넌트를 서버에서 내려줄지, 프론트에서 내려줄지 <br>

주요 원칙 - 기본 동작은 서버 컴포넌트 이다 <br>
Next.js의 컴포넌트는 기본적으로 서버 컴포넌트로 동작합니다.<br>
→ 별도의 선언 없이도 서버에서 렌더링됩니다.<br><br>


클라이언트 컴포넌트가 필요한 경우 'use client' 선언이 필요하다 <br>

보통 아래와 같은 상황에서 'use client' 를 선언한다 <br>
```java
React의 클라이언트 전용 훅(useState, useEffect 등)을 사용.
브라우저 이벤트 처리 (예: 클릭 이벤트).
상태 관리 라이브러리 사용 (예: Redux, Zustand 등).
```

### API 통신과의 관계

API 통신이 서버-사이드에서만 이루어진다면(예: getServerSideProps, getStaticProps), 'use client'는 필요하지 않다 <br>
API 통신이 클라이언트-사이드에서 이루어진다면(예: fetch를 useEffect 내에서 호출), 'use client'가 필요합니다. <br>

#### API 통신이 없는 경우
클라이언트 컴포넌트로 동작할 이유가 없다면, 'use client'를 선언할 필요가 없습니다. <br>
어차피 기본적인 default 는 'use server' 이기 때문에 기본적으로 서버에서 처리될 수 있으므로 불필요한 선언을 줄이는 것이 좋다 <br><br>


ex) 서버 컴포넌트 예시 (API 통신 없음)
```jsx
'use server'

export default function ServerComponent() {
  return <h1>이 컴포넌트는 서버에서 렌더링됩니다.</h1>;
}
```

<br>

ex) 클라이언트 컴포넌트 예시 (API 통신 없음, 클라이언트 전용 훅 사용)
```java
import { useState } from 'react';

export default function ClientComponent() {
  const [count, setCount] = useState(0);

  return (
    <div>
      <button onClick={() => setCount(count + 1)}>Click Me</button>
      <p>Count: {count}</p>
    </div>
  );
}
```

### 결론
API 통신 여부는 'use client'나 'use server' 선언과 직접적인 연관은 없다 <br>
컴포넌트가 클라이언트에서 실행되어야 하는 이유가 없다면, 선언하지 않아도 된다 <br><br>

불필요한 'use client' 선언은 서버 컴포넌트의 장점 을 이용하지 못할 수 있으니 이벤트 혹은 리액트 훅이 필요한 경우에만 사용하는게 맞다