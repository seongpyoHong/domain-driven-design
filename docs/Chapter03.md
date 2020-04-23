## Dependency Injection & Aspect-Oriented Programming

어플리케이션은 종료되는 순간 모든 정보들은 날아가버린다. 하지만 이런 문제를 해결하기 위해서 모든 정보를 기억하고 시작 시 복구하는 작업은 많은 자원을 낭비한다. 가장 최적화 된 방법은 당장 처리해야 할 최소한의 정보만 기억하는 방법이고 이를 실현하기 위한 속성을 **영속성 (Persistence)** 라고 한다.

**Order**
```java
Order(String orderId, Customer customer) {
    super(orderId);
    this.customer = customer;
}

public Order with(String productName, int quantity) throws OrderLimitExceededException {
    return with(new OrderLineItem(productName, quantity));
    }

```

Order와 OrderLineItem의 생성자가 호출되는 순간 사용자가 입력한 주문 정보를 저장하고 있는 주문 Aggregate가 생성된다. 생성된 주문 객체가 어플리케이션의 생명주기 동안 동일한 객체로 참조되기 위해서는 Repository에 의해 관리되어야 한다.

이번에는 생성된 주문 내역을 삭제하는 시나리오를 구현해보자.  조심해야할 점은 Repository 관점에서 삭제는 객체 자체의 소멸이 아닌 해당 객체를 Reference Object로 취급하지 않겠다는 것을 의미한다. 

Repository에 삭제 메소드에 추가해보자.

**Register**
```java
public static EntryPoint delete(Class<?> entryPointClass, String objectName) {
    return soleInstance.deleteObj(entryPointClass, objectName);
}

private EntryPoint deleteObj(Class<?> entryPointClass, String objectName) {
    Map<String, EntryPoint> foundEntryPoint =
            entryPoints.get(entryPointClass);
    return foundEntryPoint.remove(objectName);
}

```

**OrderRepository**
```java
public Order delete(String orderNumber) {
    return (Order) Register.delete(Order.class, orderNumber);
}
```

이를 통해 시스템은 Repository를 통해 Reference Object의 생명 주기를 관리할 수 있다. (생성 & 삭제)

메모리는 휘발성이며 비용이 비싼 자원이기 때문에 지금 당장 필요하지 않은 정보들을 이차 저장소(Secondary Storage)로 옮겨 놓을 필요가 있다. 이와 같이 일차 저장소(ex 메모리)의 도메인 객체를 이차 저장소에 저장하는 기법을 영속성 (Persistence)이라고 한다.

일반적으로 Enterprise Application에서는 안정성이라는 이유로 인해 RDBMS를 이차 저장소로 사용한다. 하지만, 객체 지향적 사고와 관계형 데이터 데이터 베이스 간에 개념에서 발생하는 **Impedance** **Mismatch**가 발생한다. 

⇒ 이를 해결하기 위해 **객체 관계 매핑(ORM)** 을 사용한다.

**ORM은 내부적으로 Data Mapper Pattern을 사용한다. Data Mapper Pattern이란 객체 지향의 도메인 객체와 관계형 데이터베이스 테이블, 매퍼 자체의 독립성을 유지하면서 도메인 객체와 테이블 간의 데이터를 이동시키는 객체이다.**

현재까지는 메모리에 존재하는 Reference Object 컬렉션을 관리하는 용도로 Repository를 사용하였다. 이제부터는 Reference Object의 영속성을 관리하도록 변경해보자.

**Repository가 도메인 객체를 어떻게 관리하던지 상관없이 사용자 입장에서 Repository는 도메인 객체를 가지고 있는 객체 풀과 같다. 즉, Repository에서 제공하는 추상화된 도메인 객체에 대한 오퍼레이션들을 통해 사용자는 영속성 메커니즘에 대한 상세 구현을 알지 못하더라도 도메인 모델을 설계하고 식별할 수 있다.** 

⇒ 영속성 메커니즘 : Repository 내부에 구현 / 사용자 : Repository에서 제공하는 추상화 된 operation 사용

사용자는 서비스를  구현할 떄, Repository의 내부 구현(DB)에 대해 고려하지 않지만, Repository를 구현할 때에는 데이터 소스에 대한 고려가 필요하다. 

지금까지 구현한 주문 어플리케이션에 영속성 메커니즘을 추가하기 위해서는 Repository의 내부 구현을 바꿔야한다. 

**OrderLineItem**
```java
public class OrderLineItem {
    private ProductRepository productRepository = new ProductRepository();

  public OrderLineItem(String productName, Integer quantity) {
      this.product = productRepository.find(productName);
      this.quantity = quantity;
  }
}
```

OrderLineItem은 ProductRepository를 클래스 로딩 시 직접 생성한다. Reference Object에 대한 관리를 DB에서 하기 위해  ProductRepository의 내부 코드(영속성 메커니즘)를 수정할 경우,  OrderLineItem은 ProductRepository에 의존적이기 때문에 Database에도 의존적이게 되어버린다. 또한 OrderLineItem을 사용하는 모든 도메인 클래스들 또한 Database에 의존적이게 된다.

⇒ 객체간의 결합도가 높아진다.  (OrderLineItem과 ProductRepository가 직접적으로 연관되어있다.)

**객체간의 결합도를 낮추는 일반적인 방법은 직접적인 의존 관계를 제거하고 두 클래스가 추상에 의존하도록 설계를 수정하는 것이다. 즉,  구체 클래스 간의 의존 관계를 추상 계층을 통해 분리함으로써 해결할 수 있다.**

먼저, ProductRepository를 인터페이스와 구현 클래스로 분리한다.

**ProductRepository**
```java
public interface ProductRepository {
    void save(Product product);
    Product find(String productName);
}
```
 
**ProductRepositoryImpl**
```java
public class ProductRepositoryImpl implements ProductRepository {
    @Override
    public void save(Product product) {
        Register.add(Product.class, product);
    }

    @Override
    public Product find(String productName) {
        return (Product) Register.get(Product.class, productName);
    }
}
```

**OrderLineItem**

OrderLineItem의  ProductRepository 생성 부분을 추상에 의존하도록 수정한다.
```java
public class OrderLineItem {
    ...
    private ProductRepository productRepository = new ProductRepositoryImpl();
    ...
}
```

추상에 의존하도록 수정하였지만 OrderLineItem과 ProductRepositoryImpl은 여전히 강하게 결합되어있다. **그 이유는 객체의 구성(Configuration)과 사용(Use)이 한 곳에 공존하고 있기 때문이다.**

이를 해결하기 위해 외부의 객체가 OrderLineItem과 ProductRepositoryImpl 간의 관계를 설정하도록 함으로써 구성과 사용을 분리해야한다.

⇒ **의존성 주입 (Dependency Injection)**

DI를 수행하는 Infrastructure 코드를 직접 작성할 수도 있지만, 경량화된 컨테이너인 Spring을 사용하여 구축해보기로 한다.

먼저 OrderLineItem, ProductRepository를 `@Component` 를 통해 Bean으로 등록한다.

**OrderLineItem** 
```java
public class OrderLineItem {

...

@Autowired
private ProductRepository productRepository;

...

}

```

`@Autowired` 어노테이션을 통해  Spiring Application Context에 등록된 (Spring이 생명주기를 관리하는) Bean인 경우, 의존성을 주입할 수 있다.   

Spring Framework는 컨테이너에서 관리할 객체를 등록할 떄 객체의 인스턴스를 하나만 유지할 지 필요 시 매번 새로운 인스턴스를 생성할지 정의할 수 있다. 따라서 static 메소드를 사용하지 않더라도 객체를 Singleton으로 유지할 수 있다.  static 메소드는 오바리이딩이 불가능하고 결합도가 높아지게 만들기 때문에 사용을 지양한다.

따라서 Spring Framework를 사용하면 Singleton으로 구현된 Register의 인터페이스와 구현부를 분리하여 낮은 결합도와 높은 유연성을 제공할 수 있다.

**Register**
```java
public interface Register {
    void init();
    void add(Class<?> entryPointClass, EntryPoint newObj);
    EntryPoint get(Class<?> entryPointClass, String objName);
    Collection<? extends EntryPoint> getAll(Class<?> entryPointClass);
    EntryPoint delete(Class<?> entryPointClass, String objName);
}
```

**RegisterImpl**
```java
@Component("register")
public class RegisterImpl implements Register {
    private Map<Class<?>, Map<String, EntryPoint>> entryPoints;

    public RegisterImpl() {
        init();
    }

    @Override
    public void init() {
        entryPoints = new HashMap<>();
    }

    @Override
    public void add(Class<?> entryPointClass, EntryPoint newObj) {
        Map<String, EntryPoint> foundedEntryPoint = entryPoints.computeIfAbsent(entryPointClass, k -> new HashMap<>());
        foundedEntryPoint.put(newObj.getIdentity(), newObj);
    }

    @Override
    public EntryPoint get(Class<?> entryPointClass, String objName) {
        Map<String, EntryPoint> foundedEntryPoint = entryPoints.computeIfAbsent(entryPointClass, k -> new HashMap<>());
        return foundedEntryPoint.get(objName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<? extends EntryPoint> getAll(Class<?> entryPointClass) {
        Map<String, EntryPoint> foundedEntryPoint = entryPoints.get(entryPointClass);
        return (Collection<? extends EntryPoint>)Collections.unmodifiableCollection(foundedEntryPoint != null ? entryPoints.get(entryPointClass).values() : Collections.EMPTY_SET);
    }

    @Override
    public EntryPoint delete(Class<?> entryPointClass, String objName) {
        Map<String, EntryPoint> foundedEntryPoint = entryPoints.get(entryPointClass);
        return foundedEntryPoint.remove(objName);
    }
}
```
    

이를 통해 ProducRepositoryImpl는 Register 인터페이스에 의존할 수 있다.

**ProductRepositoryImpl**
```java
@Component("productRepository")
public class ProductRepositoryImpl implements ProductRepository {

    @Autowired
    private Register register;

    @Override
    public void save(Product product) {
        register.add(Product.class, product);
    }

    @Override
    public Product find(String productName) {
        return (Product) register.get(Product.class, productName);
    }
}
```
    

ProductRepository를 인터페이스와 구현클래스로 분리한 것과 같이 OrderRepository와 CusomterRepository를 리팩토링 해보자.

**OrderRepository**
```java
public interface OrderRepository {
    Set<Order> findByCustomer(Customer customer);
    Set<Order> findAll();
    Order delete(String orderNumber);
}
```

**OrderRepositoryImpl**
```java
    @Component("orderRepository")
    public class OrderRepositoryImpl {
        @Autowired
        private Register register;
        
        public Set<Order> findByCustomer(Customer customer) {
            Set<Order> results = new HashSet<Order>();
            for (Order order : findAll()) {
                if (order.idOrderBy(customer)) {
                    results.add(order);
                }
            }
            return results;
        }
    
        public Set<Order> findAll() {
            return new HashSet<Order>((Collection<? extends Order>) register.getAll(Order.class));
        }
    
        public Order delete(String orderNumber) {
            return (Order) register.delete(Order.class, orderNumber);
        }
    }
```

**CustomerRepository**

    public interface CustomerRepository {
        void save(Customer customer);
        Customer find(String identity);
    }

**CustomerRepositoryImpl**
```java
public class CustomerRepositoryImpl implements CustomerRepository{
    @Autowired
    private Register register;
    
    public void save(Customer customer) {
        register.add(Customer.class, customer);
    }
    public Customer find(String identity) {
       return (Customer) register.get(Customer.class, identity);
    }
}
```

위와 같이 코드를 작성하는 것이 맞는 것처럼 보이지만, 실제로 실행해보면  OrderLineItem은 Spring Application Context의해 관리되지 않는 객체이므로 ProductRepository 객체를 의존성 주입받을 수 없다.

Spring Framework의 컨테이너 외부에서 의존성을 주입을 제공하는 가장 효율적인 방법은 AOP를 적용하는 것이다. AOP를 통해 시스템의 관심사를 분리하여 결합도가 낮고 재사용이 가능한 시스템을 개발할 수 있다. AOP의 기는 중 Load-Time Weaving (LTW) 기능을 사용하는 것으로 Spring 컨텍스트 외부 객체의 클래스 로더가 클래스를 로드할 때 바이트 코드를  수정하여 Spring 빈을 삽입하는 것이 가능하다. (Spring Bean 주입은 Runtime Weaving)

[https://jehuipark.github.io/java/generic-object-di-try-with-spring](https://jehuipark.github.io/java/generic-object-di-try-with-spring)를 참고

**build.gradle**
```groovy
    plugins {
        id 'java'
        id 'org.springframework.boot' version '2.1.6.RELEASE'
    }
    apply plugin: 'io.spring.dependency-management'
    group 'org.example'
    version '1.0-SNAPSHOT'
    
    sourceCompatibility = 1.8
    
    repositories {
        mavenCentral()
    }
    
    dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
        implementation 'org.springframework.boot:spring-boot-starter-aop'
        implementation 'org.springframework:spring-instrument'
        runtimeOnly 'com.h2database:h2'
    
        compileOnly "org.projectlombok:lombok:1.16.16"
        testImplementation('org.springframework.boot:spring-boot-starter-test') {
            exclude module: 'junit'
        }
        testImplementation('org.junit.jupiter:junit-jupiter-api:5.2.0')
        testCompile('org.junit.jupiter:junit-jupiter-params:5.2.0')
        testRuntime('org.junit.jupiter:junit-jupiter-engine:5.2.0')
    }
    
    File instrumentLibPath = file{
        return sourceSets.getByName("main").compileClasspath.find {
            cls -> return cls.getName().contains("spring-instrument")
        }
    }
    
    test{
        jvmArgs "-javaagent:${instrumentLibPath}"
        useJUnitPlatform()
    }
    
    
    bootRun {
        doFirst {
            jvmArgs "-javaagent:${instrumentLibPath}"
        }
    }
```

LTW을 사용하기 위해서는 spring-agent를 통해 jvm에 로드되어야 하고 이를 위해  jvm agent옵션으로 spring-instrument 라이브러리 경로를 넘겨주어야 한다. 

**App**
```java
@SpringBootApplication
@EnableSpringConfigured
@EnableLoadTimeWeaving
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class,args);
    }
}
```

- `@EnableLoadTimeWeaving` 어노테이션은 로드타임위빙이 가능하도록 만든다.
- `@EnableSpringConfigured` 어노테이션은 일반클래스 또한 스프링설정을 주입받는게 가능하도록 만든다.

**OrderLineItem**
```java    
@Configurable(autowire = Autowire.BY_TYPE, value = "orderLineItem", preConstruction = true)
public class OrderLineItem {
        ...
}
```

preConstruction = true 설정을 통해  생성자가 호출하기 이전에 의존성이 주입되도록 설정한다. 

Test를 돌려보면
```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
class OrderTest {
    private Customer customer;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Register register;

    @BeforeEach
    public void setUp() {
        register.init();
        productRepository.save(new Product("상품1", 1000));
        productRepository.save(new Product("상품2", 5000));
        customer = new Customer("CUST-01", "sphong", "korea", 200000);
    }

    @Test
    public void testOrderPrice() throws OrderLimitExceededException {
        Order order = customer.newOrder("CUST-01-ORDER-01")
                .with("상품1",10)
                .with("상품2",20);
        orderRepository.save(order);
        assertEquals(new Money(110000), order.getTotalPrice());
    }
}
```

테스트가 성공적으로 통과하였다. 

지금까지 수행한 리팩토링을 통해 도메인 클래스들이 Repository의 인터페이스에만 의존할 뿐 구현 클래스에 의존하지 않도록 되었으며, Data Source의 변경에 영향을 받지 않는다.