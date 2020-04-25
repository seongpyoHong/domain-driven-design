### Architecture
Domain Driven Design을 적용한 어플리케이션의 패키지 구조는 어떻게 될까?
다양한 선례들이 존재하지만, 대부분의 예시들이 4 Layer의 구조를 제시하고 있다.

[설계방법론에 대한 글](https://kuleeblog.wordpress.com/2017/01/19/java-spring-ddd-domain-driven-design-설계-방법론-2-project-packaging/)을 참고하였다.


- Interface Layer
    
  interface Layer에는 controller와 각동 데이터 제어를 위한 facade가 포함된다.
  
  - validation 
  - view mapping
  - object converting 등
  
- Application Layer

    
- Domain Layer

    **저장 데이터를 제어하는 model 패키지**
     
     @Entity 와 같은 hibernate class를 직접 넣어주거나, Domain class를 넣어 domain 기능을 class에 넣어주고 실제 디비 데이터는 infrastructure에서 저장
    
    **기능을 제어하는 service 패키지**
     
     DDD 설계중 서비스 파트 Interface 포함
    
- Infrastructure Layer
   - Spring의 @Configuration 파일 포함
   - Spring의 Injection 기능을 사용해 domain layer나 application layer에 존재하는 Interface의 실제 구현체 포함
   - 이때 패키지명을 각 service나 application의 이름을 활용해 infrastructure에서 구현하는 interface가 어떤 것인지 패키지명으로 확인할 수 있도록 하면 좋다.
 
