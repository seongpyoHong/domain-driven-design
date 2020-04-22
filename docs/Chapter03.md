## Dependency Injection & Aspect-Oriented Programming

어플리케이션은 종료되는 순간 모든 정보들은 날아가버린다. 하지만 이런 문제를 해결하기 위해서 모든 정보를 기억하고 시작 시 복구하는 작업은 많은 자원을 낭비한다. 가장 최적화 된 방법은 당장 처리해야 할 최소한의 정보만 기억하는 방법이고 이를 실현하기 위한 속성을 **영속성 (Persistence)** 라고 한다.

**Order**

    Order(String orderId, Customer customer) {
        super(orderId);
        this.customer = customer;
    }
    
    public Order with(String productName, int quantity) throws OrderLimitExceededException {
        return with(new OrderLineItem(productName, quantity));
    }

Order와 OrderLineItem의 생성자가 호출되는 순간 사용자가 입력한 주문 정보를 저장하고 있는 주문 Aggregate가 생성된다. 생성된 주문 객체가 어플리케이션의 생명주기 동안 동일한 객체로 참조되기 위해서는 Repository에 의해 관리되어야 한다.

이번에는 생성된 주문 내역을 삭제하는 시나리오를 구현해보자.  조심해야할 점은 Repository 관점에서 삭제는 객체 자체의 소멸이 아닌 해당 객체를 Reference Object로 취급하지 않겠다는 것을 의미한다. 

Repository에 삭제 메소드에 추가해보자.

**Register**

    public static EntryPoint delete(Class<?> entryPointClass, String objectName) {
        return soleInstance.deleteObj(entryPointClass, objectName);
    }
    
    private EntryPoint deleteObj(Class<?> entryPointClass, String objectName) {
        Map<String, EntryPoint> foundEntryPoint =
                entryPoints.get(entryPointClass);
        return foundEntryPoint.remove(objectName);
    }

**OrderRepository**

    public Order delete(String orderNumber) {
        return (Order) Register.delete(Order.class, orderNumber);
    }

이를 통해 시스템은 Repository를 통해 Reference Object의 생명 주기를 관리할 수 있다. (생성 & 삭제)

메모리는 휘발성이며 비용이 비싼 자원이기 때문에 지금 당장 필요하지 않은 정보들을 이차 저장소(Secondary Storage)로 옮겨 놓을 필요가 있다. 이와 같이 일차 저장소(ex 메모리)의 도메인 객체를 이차 저장소에 저장하는 기법을 영속성 (Persistence)이라고 한다.

일반적으로 Enterprise Application에서는 안정성이라는 이유로 인해 RDBMS를 이차 저장소로 사용한다. 하지만, 객체 지향적 사고와 관계형 데이터 데이터 베이스 간에 개념에서 발생하는 **Impedance** **Mismatch**가 발생한다. 

⇒ 이를 해결하기 위해 **객체 관계 매핑(ORM)**을 사용한다.

**ORM은 내부적으로 Data Mapper Pattern을 사용한다. Data Mapper Pattern이란 객체 지향의 도메인 객체와 관계형 데이터베이스 테이블, 매퍼 자체의 독립성을 유지하면서 도메인 객체와 테이블 간의 데이터를 이동시키는 객체이다.**

현재까지는 메모리에 존재하는 Reference Object 컬렉션을 관리하는 용도로 Repository를 사용하였다. 이제부터는 Reference Object의 영속성을 관리하도록 변경해보자.

**Repository가 도메인 객체를 어떻게 관리하던지 상관없이 사용자 입장에서 Repository는 도메인 객체를 가지고 있는 객체 풀과 같다. 즉, Repository에서 제공하는 추상화된 도메인 객체에 대한 오퍼레이션들을 통해 사용자는 영속성 메커니즘에 대한 상세 구현을 알지 못하더라도 도메인 모델을 설계하고 식별할 수 있다.** 

⇒ 영속성 메커니즘 : Repository 내부에 구현 / 사용자 : Repository에서 제공하는 추상화 된 operation 사용

사용자는 서비스를  구현할 떄, Repository의 내부 구현(DB)에 대해 고려하지 않지만, Repository를 구현할 때에는 데이터 소스에 대한 고려가 필요하다. 

지금까지 구현한 주문 어플리케이션에 영속성 메커니즘을 추가하기 위해서는 Repository의 내부 구현을 바꿔야한다. 

**OrderLineItem**

    public class OrderLineItem {
    	private ProductRepository productRepository = new ProductRepository();
    
      public OrderLineItem(String productName, Integer quantity) {
          this.product = productRepository.find(productName);
          this.quantity = quantity;
      }
    }

OrderLineItem은 ProductRepository를 클래스 로딩 시 직접 생성한다. Reference Object에 대한 관리를 DB에서 하기 위해  ProductRepository의 내부 코드(영속성 메커니즘)를 수정할 경우,  OrderLineItem은 ProductRepository에 의존적이기 때문에 Database에도 의존적이게 되어버린다. 또한 OrderLineItem을 사용하는 모든 도메인 클래스들 또한 Database에 의존적이게 된다.

⇒ 객체간의 결합도가 높아진다.  (OrderLineItem과 ProductRepository가 직접적으로 연관되어있다.)

**객체간의 결합도를 낮추는 일반적인 방법은 직접적인 의존 관계를 제거하고 두 클래스가 추상에 의존하도록 설계를 수정하는 것이다. 즉,  구체 클래스 간의 의존 관계를 추상 계층을 통해 분리함으로써 해결할 수 있다.**

먼저, ProductRepository를 인터페이스와 구현 클래스로 분리한다.

**ProductRepository**

    public interface ProductRepository {
        void save(Product product);
        Product find(String productName);
    }

**ProductRepositoryImpl**

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

**OrderLineItem**

OrderLineItem의  ProductRepository 생성 부분을 추상에 의존하도록 수정한다.

    public class OrderLineItem {
    	...
    	private ProductRepository productRepository = new ProductRepositoryImpl();
    	...
    }

추상에 의존하도록 수정하였지만 OrderLineItem과 ProductRepositoryImpl은 여전히 강하게 결합되어있다. 그 이유는 무엇일까? (5부부터 다시)