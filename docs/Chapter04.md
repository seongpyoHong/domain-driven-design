## ORM과 투명한 영속성

Domain-Driven-Design은 도메인 모델을 고객의 용어로 표현하고 도메인과 무관한 기술적인 부분에 대해서는 이야기 하지 않으며 **도메인 모델과 소프트웨어 모델간의 표현적 차이를 최소화 하기 위한 접근 방법**이다.

DDD를 성공적으로 적용하기 위해서는 기본적으로 2가지 요소가 갖추어져야 한다. 

1. **Ubiquitous Language**

    고객과 개발자들 사이에 공통된 용어를 사용하도록 함으로써 의사소통의 단절 및 오해로 인해 잘못된 소프트웨어가 개발되는 것을 방지한다.

2. **Model-Driven-Design**

    분석, 설계, 구현의 모든 단계를 관통하는 하나의 모델을 만들자는 개념 (표현적 차이를 줄암으로써 소프트웨어가 도메인의 모습을 투영하도록 개발)

    **⇒ 침투적인 EJB보다 비침투적인 POJO 기반의 경량 프레임워크를 적용하는 것이 바람직하다.**

**POJO vs EJB ?**

3장에서 수행한 리팩토링을 통해 Infrastructure의 변경이 도메인 모델에 영향을 미치지 않도록 구조를 만들었다. 하지만 RDBMS를 도입하기 위해서 몇 가지 구성을 추가해야 한다.

**Entity**

지금까지 시스템 내에 유일하게 존재하고 상태를 추적할 수 있는 도메인 객체를 **Reference Object**라고 언급했다. 하지만, Reference Object는 용어가 도메인의 구현을 객체지향언어라는 틀로 한정하기 때문에 앞으로는 광범위한 구현 기술을 수용할 수 있는 **Entity**라는 용어를 사용하기로 한다. 

Entity의 개념은 Reference Object와 동일하지만, 1가지 다른 점이 존재한다. 외부 저장소를 어플리케이션에 연동했을 때, 도메인 객체는 메모리 상의 객체 → 데이터 베이스 레코드 → 타 시스템의 객체 ... 와 같이 다양한 형태로 변경된다. 기존 Reference Object는 동일한 객체임을 `==` 을 통한 주소값 비교로 한정했기에 도메인 객체와 객체 자체의 생명주기를 혼동할 여지가 존재한다. 때문에 도메인 객체의 표현 형식을 초월한 동일한 도메인 개념의 추적성과 유일성을 강조하는 Entity 라는 용어를 사용함으로써 도메인 객체의 생명주기를 객체 자체의 생명주기의 혼동을 피하고자 한다.

- Identity of Entity

    생명주기에 걸쳐 구현 기술과 무관하게 각 Entity의 유일성을 보장할 수 있는 속성을 식별자로 삼아야 한다. ⇒ **DB의 Primary Key**

도메인을 시스템 개발의 주도적 위치로 격상시키기 위해서는 도메인 객체를 Entity의 개념에서 바라볼 필요가 있다. ⇒ Repository 사용

**Repository는 Entity의 생성과 소멸 시점 사이를 책임지는 생명주기 관리 객체로써 Entity의 유일성을 보장받을 수 있도록 해준다.** 

**ORM**

상태와 행위를 함께 가지는 풍부한 객체 모델로 도메인 레이어를 구성하는 것을 Domain Model Pattern이라고 한다. Domain Model Pattern은 객체지향의 모든 특징을 활용하기 때문에 영속성 메커니즘에 주로 사용되는 RDBMS와의 Impedance Mismatch가 발생한다. 

⇒ 도메인 로직을 처리하는 Domain Layer와 영속석 로직을 처리하는 Persistence Layer 간의 불일치를 조정하는 **Data Mapper**를 도입, Data Mapper를 구현한 소프트웨어를 **ORM**이라고 한다.

**ORM은 다음과 같은 특징을 가진다.**

- ORM을 통해 도메인 객체는 외부의 영속성 메커니즘에 독립적인 특징을 가지며 이를 **투명한 영속성(Transparent Persistence)**라고 한다.
- 정교한 ORM은 로드된 객체들의 상태 상태 변경을 자동으로 감지하고 트랜잭션 커밋 시에 변경된 내용을 데이터베이스에 저장한다. ORM 내부에서는 Unit of Work가 현재 트랜잭션 내에서 비즈니스 로직에 의해 수정된 객체들의 집합을 유지하고 실제로 변경된 객체들에 대해서만 update를 수행한다. 이를 **더티 체킹(Dirty Checking)** 이라고 한다.
- ORM은 동일 트랜잭션 내에서 동일한 객체들이 한 번만 로드될 수 있도록 Identity Map을 유지한다. 그리고 이를 캐싱하여 성능 향상 및 동일성을 보장해준다.
- 객체들이 최초에 로드될 때, 필요한 객체만 로드한 후 나머지 객체들은 연관 관계를 통해 필요 시점에 로딩하는 것이 효율적이다. 이를 **Lazy Loding**이라고 한다.

**영속상태 vs 비영속 상태**

비영속 상태 : 메모리 상의 객체로만 존재하고 DB와의 관계각 맺어지지 않은 상태

영속 상태 : DB와의 연관 관계를 가지고 있는 상태

**영속성 전이**

ORM은 영속 객체와 연관된 객체들에게 영속성이 전이되는 **영속성 전이**라는 특징을 지원한다. 이는 **Aggregate을 전체가 하나의 단위로 처리되며, 내부 객체들이 Entry Point의 생명주기에 종속되어 불변식이 보장**되도록 한다.

**Hibernate**

Hibernate는 Java에서 사용되는 ORM의 표준으로 Spring Framework에서는 hibernate를 편리하게 사용할 수 있는 여러가지 지원 클래스를 제공한다. 

Hibernate는 XML 형식과 Annotation 형식의 Metadata Mapping을 지원하는데, 여기서는 Annotation 방식을 사용한다. 

지금까지의 도메인 모델은 EntryPoint라는 Infrastructure(Register)과 결합되어 있었다. 하위 Infrastructure과 독립적인 도메인 모델로 리팩토링 하기 위해 Order과 EntryPoint 간의 상속관계를 제거하자.

**Order** 

EntryPoint와의 상속 관계를 제거하였기 때문에 Entity의 추적성을 보장하기 위해 Order의 identity field(primary key)를 추가하였다.
```java
public class Order {
    private Long id;
    private String orderId;
    private Set<OrderLineItem> lineItems = new HashSet<>();
    private Customer customer;

    public static Order order(String orderId, Customer customer) {
        return new Order(orderId, customer);
    }

    Order(String orderId, Customer customer) {
        this.orderId = orderId;
        this.customer = customer;
    }
        
        public Long getId() {
        return id;
    }
        public String getOrderId() {
        return orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId.equals(order.getOrderId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
```

**OrderLineItem**

Long 타입의 Identity Field를 추가하고,  동일성을 판단하기 위한 equals()와 hashCode()를 오버라이딩 한다. 이 때, 하나의 Product에 대해 하나의 OrderLineItem이 존재해야 한다는 도메인 규칙이 있으므로 product와 quantity를 사용해 비교를 수행한다.
```java
@Configurable(autowire = Autowire.BY_TYPE, value = "orderLineItem", preConstruction = true)
public class OrderLineItem {
    private Long id;
    private Product product;
    private Integer quantity;

    @Autowired
    private ProductRepository productRepository;
        
        ...
        
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLineItem that = (OrderLineItem) o;
        return Objects.equals(product, that.product) &&
                Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity);
    }
}
```

해당 클래스를 DB에 매핑하기 위해 Annotation 방법을 사용한다. (여기부터 다시)