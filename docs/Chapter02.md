## **Chapter 02 : `Aggregate` & `Repository`**

다음과 같은 상풐 주문에 관한 도메인 모델을 생각해보자.

고객은 시스템을 사용해서 상품을 주문한다. 한 번 주문 시 다수의 상품을 구매할 수 있으며 상품에 대한 이름, 가격과 같은 기본 정보는 별도의 상품 클래스에 정의되어 있다. 고객을 고객 등급에 따라 1회 주문 시 구매 가능한 금액에 제한을 받는다.

Reference Object는 **독립적인 클래스**로 표기 / Value Object는 **클래스의 속성**으로 표기

좋은 모델이란?
충분한 정보를 다루면서도 세부 사항에 집착하지 않고 핵심 주제를 효과적으로 전달하는 모델

주문 시 고객은 정해진 등급에 따른 구매 가능 금액 내에서 상품을 구매할 수 있다. 이 로직을 어디서 관리해야 할까?

1. 고객 객체에서 관리

    고객 객체에서 주문 금액이 구매 가능 금액을 초과하는지 확인하기 위해서 내부 상태(주문 금액)을 알아야한다. 따라서 **고객과 주문 간에 양방향 연관관계가 발생한다.
    ⇒ 양방향 연관관계는 도메인 모델 간의 결합도를 높이고 관계 간의 일관성을 유지하기 위해 필요한 구현 복잡도를 증가시키기 때문에 지양하도록 한다.**

    - Feature Envy

        메서드가 자신의 내용보다 다른 클래스의 내용에 더 관심을 가지는 현상으로 객체 간의 결합도가 높아지는 문제를 야기한다.

    - Information Expert

        정보를 가지고 있는 클래스에 책임을 할당하는 패턴

2. 주문 객체에서 관리

    주문 객체에서 구매 가능 금액을 초과하는지 확인하기 위해서 고객 객체의 내부 상태(구매 가능 금액)을 알아야한다. 이미 주문 객체에서 고객 객체로의 연관관계가 설정되어 있기 때문에 

    Information Expert 패턴을 위배하지 않는다.

이번에는 주문에 주문 항목을 추가하는 시나리오를 생각해보자. 

1. 고객 상품 선택 및 개수 입력
2. 주문 항목 생성 (상품 & 개수)
3. 주문은 주문항목의 가격을 통해 주문 총액과 구매 고객의 한도액 비교
4. 한도액 초과식 예외 발생 후 종료

여기서 **주문 객체와 주문 항목 객체는 구매액이 고객의 주문 한도액을 초과할 수 없다는 불변식을 공유하는 하나의 논리적 단위**이다.

⇒ 이 불변식을 유지하기 위해서는 주문에 주문 항목이 추가된 이후에 외부에서 직접적으로 주문 항목을 수정할 수 없고 오직 주문의 제어 하에 수정되어야 한다. (**캡슐화**)

주문 - 주문 항목이 하나의 논리적 단위로 취급하기로 결정하였다. 그렇다면 상품 객체는 이 논리적 단위에 포함되어야 하는지 생각해보자.

- 동시성 측면

    다중 사용자 환경에서 주문 항목과 연관된 상품 객체를 처리할 때, 상품은 하나 이상의 주문에 의해 참조되기 때문에 주문을 잠글 때마다 연결된 모든 상품을 함께 잠근다면 해당 상품에 접근하려는 모든 주문 객체가 동시에 대기 상태로 빠지는 결과를 낳는다. 

- 변경 빈도

    주문과 주문 항복이 변경되는 빈도에 비해 상품의 변경 빈도는 상대적으로 매우 낮다.

따라서, 주문 - 주문 항목은 하나의 객체 클러스터로 구성하며, 고객 / 상품은 주문 클러스터에 속하지 않는 독립적인 객체로 구성한다.

**이와 같이 불변식을 유지하기 위해 하나의 단위로 취급되면서 변경의 빈도가 비슷하고, 동시 접근에 대한 잠금의 단위가 되는 객체의 집합을 Aggregate라고 한다.**

Aggregate는 **Root**와 **Boundary**를 가진다.

- **Root (=Entry Point)**

    Aggregate 내에 포함된 객체 그룹을 탐색하기 위해 필요한 시작 위치

- **Boundary**

    클러스터의 포함 범위

Aggregate Pattern에 대한 규칙은 다음과 같다.

- Entry Point는 전역 식별자를 가지며 궁극적으로 불변식을 검증하는 책임을 가진다.
- Root를 제외한 Aggregate 내의 Reference Object는 외부에서 접근이 불가능하기 때문에 지역 식별자를 사용하며, Root는 전역 식별자를 가진다.
- 오직 Entry Point만이 Repository로부터 직접 얻어질 수 있으며, 다른 객체는 Entry Point로부터의 연관 관계를 통해서만 접근 가능하다.
- 삭제 오퍼레이션은 Aggregate 내부의 모든 객체를 제거해야 한다.
- Aggregate 내부의 한 객체에 대한 변경이 생기면, 전체 Aggregate에 관한 모든 불변식이 만족되어야 한다.
- 한 Reference Object가 다른 객체에 대해 독립적으로 얻어져야 한다면 이 Reference Object을 중심으로 Aggregate Boundary를 식별하고 해당 Reference Object를 Entry Point로 지정한다.

Repository는 Entry Point에 대해서만 할당한다. 

⇒ 이를 통해 객체 그룹에 대한 무분별한 접근을 지양하고, 연관 관계를 통해 접근해야 할 도메인 객체를 명확히 구분함으로 효율적으로 객체를 탐색할 수 있다.

### 코드 구현

Customer는 고객의 주문 한도 검증에 필요한 limitPrice 속성을 추가한다. 

**Customer**
```java
@Getter
public class Customer extends EntryPoint {
    private String number;
    private String name;
    private String address;
    private Long mileages;
    private Money limitPrice;

    public Customer(String number, String name, String address, Integer limitPrice) {
        super(number);
        this.number = number;
        this.name = name;
        this.address = address;
        this.limitPrice = new Money(limitPrice);
    }

    ....

    public Order newOrder(String orderId) {
        return Order.orders(orderId, this);
    }

    public boolean isExceedLimitPrice(Money money) {
        return money.isGreaterThan(limitPrice);
    }
}
```

Order는 Entry Point이기 때문에 Entry Point를 상속 받고, Create Method를 통해서만 객체를 생성할 수 있도록 제한한다. 
```java
public class Order extends EntryPoint{
    private Set<OrderLineItem> lineItems = new HashSet<>();
    private Customer customer;

    public static Order orders(String orderId, Customer customer) {
        return new Order(orderId, customer);
    }

    Order(String orderId, Customer customer) {
        super(orderId);
        this.customer = customer;
    }

}
```

주문 생성 메소드 `with()` 는 메소드 체인 방식을 적용한다.
```java
public Order with(String productName, int quantity) {
    return with(new OrderLineItem(productName, quantity));
}

private Order with(OrderLineItem lineItem) {
    if (isExceedLimit(customer, lineItem)) {
        throw new OrderLimitExceededException();
    }

    lineItems.add(lineItem);
    return this;
}
```

주문 총액이 고객의 한계 금액을 초과하는지 검증하는 책임을 진다.
```java
private Money getTotalPrice() {
      Money result = new Money(0);
      for(OrderLineItem item : lineItems) {
          result = result.add(item.getPrice());
      }

      return result;
  }

  private boolean isExceedLimit(Customer customer, OrderLineItem lineItem) {
      return customer.isExceedLimitPrice(getTotalPrice().add(lineItem.getPrice()));
  }
```

OrderLineItem은 Product와 연관관계를 가지며, Product는 Entry Point이기 때문에 ProductRepository를 이용해 접근한다.
```java
public class OrderLineItem {
    private Product product;
    private Integer quantity;

    private ProductRepository productRepository = new ProductRepository();

    public OrderLineItem(String productName, Integer quantity) {
        this.product = productRepository.find(productName);
        this.quantity = quantity;
    }

    public Money getPrice() {
        return product.getPrice().multiply(quantity);
    }

    public Product getProduct() {
        return product;
    }
}
```

Product 클래스는 다음과 같다.
```java
public class Product extends EntryPoint {
    private String name;
    private Money price;

    public Product(String name, Integer price) {
        super(name);
        this.name = name;
        this.price = new Money(price);
    }

    public Product(String name, Money price) {
        super(name);
        this.name = name;
        this.price = price;
    }

    public Money getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}
```
위의 코드는 Order 클래스에서 with를 통해 새로운 주문을 추가할 때, 동일한 상품이 있어도 다른 주문으로 인식한다. 이를 동일한 주문으로 인식하기 위해서는 주문 목록에 포함하는지 검증하는 코드를 추가해야 한다.

**Order**
```java
private Order with(OrderLineItem lineItem) {
    if (isExceedLimit(customer, lineItem)) {
        throw new OrderLimitExceededException();
    }

    for(OrderLineItem item : lineItems) {
        if (item.isProductEqual(lineItem)) {
            item.merge(lineItem);
            return this;
        }
    }
    lineItems.add(lineItem);
    return this;
}
```

**OrderLineItem**

동일한 객체인지 확인하는 작업에서 OrderLineItem의 product를 통해 비교하며, product는 Reference Object 이므로 `==` 을 통해 비교한다.
```java
public boolean isProductEqual(OrderLineItem lineItem) {
    return this.product == lineItem.product;
}

public OrderLineItem merge(OrderLineItem lineItem) {
    quantity += lineItem.quantity;
    return this;
}
```

Order 객체가 필요한 경우 Order가 Entry Point이므로 Order Repository를 통해 얻을 수 있다. 그렇다면 특정한 고객에 대한 주문 목록을 얻고자 한다면 어떻게 접근해야 할까?

1. Customer ⇒ Order 접근 

    양방향 연관 관계가 생기므로 부적합

2. OrderRepository에 Customer로 주문 목록을 조회하는 메소드를 추가

    이미 맺어져 있는 연관관계로 접근 가능

    주문 객체에 접근하기 위한 일관성 있는 방법(OrderRepository로 접근)

**OrderRepository**
```java
public class OrderRepository {
    public Set<Order> findByCustomer(Customer customer) {
        Set<Order> results = new HashSet<Order>();
        for (Order orders : findAll()) {
            if (orders.idOrderBy(customer)) {
                results.add(orders);
            }
        }
        return results;
    }

    public Set<Order> findAll() {
        return new HashSet<Order>((Collection<? extends Order>) Register.getAll(Order.class));
    }
}
```

**Order**
```java
public boolean idOrderBy(Customer customer) {
    return this.customer == customer;
}
```
