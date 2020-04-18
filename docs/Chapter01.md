## Chapter 01 : `Value Object` **vs `Reference Object`**

Application을 구성하는 객체들은 **Reference Object**와 **Value Object**로 분류할 수 있다. 

- **Reference Object**

    추적 가능한 객체를 나타낼 때 사용하며, Application 내에서 유일하게 식별이 가능해야 한다. 

    ex) 고객 정보(구매 기록 or 마일리지 적립 상태)는 계속해서 추적할 수 있어야 하기 때문에 **Reference Object**

    - **Customer**
        ```java
        @Getter
        @AllArgsConstructor
        public class Customer {
            private String number;
            private String name;
            private String address;
            private Long mileages;
        
            public void purchase(Long price) {
                this.mileages += (price / 100L);
            }
        
            public Boolean isPossibleToPayWithMileage(Long price) {
                return mileages > price;
            }
        
            public Boolean payWithMileage(Long price) {
                if (isPossibleToPayWithMileage(price)) {
                    return false;
                }
                mileages -= price;
                return true;
            }
        }
        ```

- **Value Object**

    객체를 구성하는 속성의 값에만 초점을 맞추며, 객체의 추적에는 관심을 두지 않는다. 

    ex) 가격은 금액이 일치하는지 확인만 하면 된다.

    - **Money**
        ```java
        @Getter
        public class Money {
            private Integer amount;
            public Money(Integer amount) {
                this.amount = amount;
            }
        
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Money money = (Money) o;
                return Objects.equals(amount, money.amount);
            }
        
            @Override
            public int hashCode() {
                return Objects.hash(amount);
            }
        
            public Money add(Money added) {
                this.amount += added.amount;
                return this;
            }
        }
        ```
        
객체를 식별할 때,

- Reference Object는 속성 값이 같더라도 다른 객체가 될 수 있으므로 **고유한 식별자** (Memory 상에 위치하는 경우 주소값)으로 비교해야 한다. **⇒**  `==`
- Value Object는 객체가 동일한 객체인지 보다 속성 값이 같은 지에 초점을 두고 속성 값을 비교해야 한다. **⇒** `equals()`

⇒ **Reference Object는 객체의 동일성을 Value Object는 객체의 동등성을 목표로 한다.** 

**Reference Object와 Value Object를 구분하는 목적은?** **별칭문제**

**별칭 문제**
Java에서는 하나의 객체를 서로 다른 변수가 참조할 수 있다.  이로인해 **다른 참조를 통해 객체에 접근하여 객체를 변경한 사실을 다른 쪽에서 알지 못한다면 변경된 속성들이 동일하게 유지되지 못하는 상황**이 발생한다.

Thinking In Java 3rd Edition에서는 다음과 같은 사항을 주의해야 한다고 언급한다.**
1. 인자를 전달하면 자동으로 별칭이 생성된다.
2. 지역 객체란 존재하지 않고, 지역 참조만이 존재한다.
3. 참조는 범위를 가지지만, 객체는 그렇지 않다.
4. 객체의 생명주기는 java에서 이슈가 아니다.
5. 인자에 final을 사용하더라도 객체의 상태를 변경하는 것을 막을 수 없다.

별칭 문제를 해결하기 위한 가장 좋은 방법은 **객체를 변경할 수 없는 불변 상태로 만드는 것**이다.

---
### 불변성

불변성을 가지는 클래스는 다음과 같은 규칙을 따른다.

- 객체를 변경하는 메소드를 제공하지 않는다.
- 재정의할 수 있는 메소드를 제공하지 않는다.
- 모든 필드를 **final**로 만든다.
- 모든 필드를 **private**으로 만든다.
- 가변 객체를 참조하는 필드는 배타적으로 접근해야 한다.

**객체를 불변으로 만들면 새로운 상태로 변경해야 하는 경우, 새로운 불변 객체를 생성해서 반환하기 때문에 별칭 문제를 해결할 수 있다.** 

**Value Object는 다음과 같은 이유로 인해 불변 객체로 만든다.**

1. 일반적으로 날짜, 금액과 같은 작은 개념을 의미하기 때문에 새로운 객체를 만들어 대체할 경우의 오버헤드가 적다.
2. 추적성에 초점을 두지 않기 때문에 동일한 객체를 계속 유지하고 있을 필요가 없다.

⇒ **불변 객체로 생성된 Value Object를 사용함으로써 풍부한 도메인 모델(Rich Domain Model)의 작성을 위해서는 유용하지만 비즈니스 관점에서 가치가 없는 작은 개념을 Value Object로 모델링 함으로써 추적성과 별칭 문제에 대한 부담 없이 해당 객체를 참조할 수 있도록 한다.**

**Reference Object는 객체를 지속적으로 추적하고 이로 인해 동일한 고객 객체가 시스템의 각 부분으로 전달되어야 한다.** 

**⇒ 생명 주기 제어 메커니즘 필요**

**Reference Object를 불변으로 만들고 싶다면 상태를 변경시키는 메소드를 포함시키지 않아야 한다.**

---
### 생명 주기 제어

어떤 결과 목록에서라도 접근이 가능한 Relational Database와는 다르게 객체 지향 시스템은 임의의 결과 목록에 접근할 수 있는 메커니즘이 없다.

⇒ 어떤 객체의 그룹을 사용하기 위해서는 하나의 객체로 부터 시작해 다른 객체로 이동하며 결과를 얻는다. 

**외부에서 객체 그룹에 접근하기 위한 유일한 통로(시작 지점)을 Entry Point라고 한다.**

Entry Point는 항상 Reference Object이므로 외부에서 시스템에 접근할 때마다 동일한 객체 인스턴스를 반환 받아야 한다. 

⇒ 이를 위해서는 **Entry Point의 유일성과 추적성을 유지/관리하는 특별한 객체**가 필요하다.

Entry Point 관리 인터페이스를 구성하는 방법은 두 가지가 존재한다.

- 각각의 Entry Point가 스스로 관리 인터페이스를 제공
- 별도의 객체가 Entiry Point에 대한 관리 인터페이스를 제공

두 방법 모두 Entry Poiint를 메모리 내에서 검색하기 위한 Key를 제공해야 한다.

이를 위해 자신의 검색키를 반환하는 메소드(**getIdentity()**)가진 EntryPoint Class를 상속받아 객체 생성 시 자신의 Identity를 제공하도록 강제한다.

- **EntryPoint**
    ```java
    public class EntryPoint {
        private final String identity;
    
        public EntryPoint(String identity) {
            this.identity = identity;
        }
    
        public String getIdentity() {
            return identity;
        }
    }
    ```
    
또한 Entry Point의 생명 주기를 관리할 객체인 Register Class를 작성한다. 

- **Register Class**
    ```java
    public class Register {
        private static Register soleInstance = new Register();
        private Map<Class<?>, Map<String, EntryPoint>> entryPoints;
        public static void init() {
            soleInstance.entryPoints =
                    new HashMap<Class<?>, Map<String, EntryPoint>>();
        }
    
        public static EntryPoint get(Class<? extends EntryPoint> entryPointClass, String objectName) {
            return soleInstance.getObj(entryPointClass, objectName);
        }
    
        public static void add(Class<? extends EntryPoint> entryPointClass, EntryPoint newObject) {
            soleInstance.addObj(entryPointClass, newObject);
        }
    
        public static Collection<? extends EntryPoint> getAll(Class<? extends EntryPoint> entryPointClass) {
            return soleInstance.getAllObjects(entryPointClass);
        }
    
        private EntryPoint getObj(Class<? extends EntryPoint> entryPointClass, String objectName) {
            Map<String, EntryPoint> foundEntryPoint =
                    entryPoints.get(entryPointClass);
            return foundEntryPoint.get(objectName);
        }
    
        private void addObj(Class<? extends EntryPoint> entryPointClass, EntryPoint newObject) {
            Map<String, EntryPoint> foundEntryPoint =
                    entryPoints.get(entryPointClass);
            if (foundEntryPoint == null) {
                foundEntryPoint = new HashMap<>();
                foundEntryPoint.put(newObject.getIdentity(), newObject);
            }
            foundEntryPoint.put(newObject.getIdentity(), newObject);
        }
    
        @SuppressWarnings("unchecked")
        private Collection<? extends EntryPoint> getAllObjects(Class<? extends EntryPoint> entryPointClass) {
            Map<String, EntryPoint> foundEntryPoints =
                    entryPoints.get(entryPointClass);
            return (Collection<? extends EntryPoint>) Collections.unmodifiableCollection(foundEntryPoints != null ?
                    entryPoints.get(entryPointClass).values() :
                    Collections.EMPTY_SET);
        }

    ```
    
1. Entry Point가 스스로 객체를 관리하기 위해서 컬렉션 저장 & 조회 메서드를 직접 제공
    - **Entry Point**
        ```java
        public class EntryPoint {
                        .
                        .
            public EntryPoint persist() {
              Register.add(this.getClass(), this);
              return this;
            }
                        .
                        .
        }
        ```
        
    - **Customer**
        ```java
        @Getter
        public class Customer extends EntryPoint {
            private String number;
            private String name;
            private String address;
            private Long mileages;
        
            public Customer(String number, String name, String address) {
                super(number);
                this.number = number;
                this.name = name;
                this.address = address;
            }
            
                /* 생성 */
                public static Customer persist() {
                    return (Customer)super.persist();
                }
        
                /* 조회 */
                public static Customer find(String number) {
                    return (Customer)Register.get(Customer.class, number)
                }
                
            public void purchase(Long price) {
                this.mileages += (price / 100L);
            }
        
            public Boolean isPossibleToPayWithMileage(Long price) {
                return mileages > price;
            }
        
            public Boolean payWithMileage(Long price) {
                if (isPossibleToPayWithMileage(price)) {
                    return false;
                }
                mileages -= price;
                return true;
            }
        }

        ```
        
2. Entry Point를 관리하는 별도의 객체인 **Repository** 객체를 사용하여 컬렉션 관리
    - **Repository (Entry Point는 검색을 위한 키 값만 제공)**
    ```java
    public class CustomerRepository {
        public void save(Customer customer) {
            Register.add(Customer.class, customer);
        }
        public Customer find(String identity) {
           return (Customer) Register.get(Customer.class, identity);
        }
    }
    ```
    
Reference Object와 Value Object는 도메인 영역을 추상화 시켜 문제 해결에 필요한 핵심 개념만을 끌어 안음으로써 문제영역의 복잡성을 감소 시키는 것을 목적으로 한다.

- Reference Object를 식별해 시스템 핵심 개념의 생명주기에 초점을 맞출 수 있다.
- Value Object를 식별해 도메인의 일부지만 추적이 필요없는 객체를 걸러낼 수 있다.
