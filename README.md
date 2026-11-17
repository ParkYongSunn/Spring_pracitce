# 0204APICompli – 도서관 API 실습 프로젝트

Spring Boot + Spring Data JPA + H2 로 만든 간단한 **도서관(저자·책) REST API** 학습용 프로젝트입니다.
빈 등록(`@Configuration`/`@Bean`), 의존성 주입, JPA 연관관계(`@OneToMany`/`@ManyToOne`), 지연 로딩, Auditing, 트랜잭션, 시작 시 시드 데이터 적재(`ApplicationRunner`) 흐름을 연습합니다.

## 기술 스택

| 항목 | 내용 |
|---|---|
| Language | Java 25 (Gradle toolchain) |
| Framework | Spring Boot 4.1.2 (`spring-boot-starter-webmvc`) |
| Persistence | Spring Data JPA, H2 (인메모리, 별도 설정 없음) |
| 기타 | Lombok, JUnit 5 |
| Build | Gradle (`./gradlew`) |

## 실행 방법

```bash
./gradlew bootRun     # 실행 (기본 포트 8080)
./gradlew test        # 테스트
```

## 패키지 구조

```
src/main/java/com/kdt
├── _204apicompli   # 애플리케이션 진입점, 연관관계 수동 검증용 main
├── Config          # 빈 설정
├── Controller      # REST 컨트롤러 (HTTP 진입점)
├── Entity          # JPA 엔티티 (Author, Book)
├── Library         # 도서관 정책 객체
├── Repository      # Spring Data JPA 리포지토리
├── Runner          # 앱 시작 시 실행되는 ApplicationRunner
└── Service         # 비즈니스 로직 / 트랜잭션
```

## 전체 흐름

```
HTTP 요청
  └─ Controller (@RestController, @GetMapping)
        └─ Service (@Service, @Transactional)
              └─ Repository (JpaRepository)
                    └─ Entity (Author 1 ─ N Book)  ── H2 DB
```

앱 시작 시 흐름:

1. `Application.main` → `SpringApplication.run` (컴포넌트 스캔, 자동 설정)
2. `LibraryConfig` 가 `LibraryPolicy` 빈("KDT 도서관", 제목 최대 20자) 등록
3. `LibrarySeedRunner` 가 저자/책 시드 데이터 저장 (이미 있으면 skip)
4. `LibraryReadyRunner` 가 `library ready : ...` 로그 출력

## 클래스 설명

### 진입점 (`_204apicompli`)
- **`Application`** – `@SpringBootApplication` 이 붙은 시작 클래스.
- **`LibraryRelationCheckMain`** – 스프링 없이 `Author`↔`Book` 양방향 연관관계 편의 메서드(`addBook`/`removeBook`)를 콘솔로 검증하는 일반 `main`. (중복 추가 방지, 다른 저자로 이동 차단, 제거 시 연관 해제)
- **`ApplicationTests`** – `@SpringBootTest` 로 컨텍스트 로딩만 확인.

### 설정 / 정책
- **`LibraryConfig`** (`@Configuration`) – `@Bean libraryPolicy()` 로 `LibraryPolicy("KDT 도서관", 20)` 을 싱글톤 빈으로 등록.
- **`LibraryPolicy`** – 도서관 이름(`libraryName`)과 제목 최대 길이(`maxTitleLength`)를 가진 불변 값 객체 (`@Getter`).

### 엔티티 (`Entity`)
- **`Author`** – 테이블 `lib_author`.
  - `name`(유니크, 30자), `country`(30자)
  - `books` : `@OneToMany(mappedBy="author", fetch=LAZY, cascade=ALL, orphanRemoval=true)` – 연관관계의 주인은 `Book`
  - `getBooks()` 는 수정 불가 리스트 반환
  - `addBook` / `removeBook` : 양방향 연관관계 편의 메서드 (null 금지, 다른 저자 책 이동 금지)
- **`Book`** – 테이블 `lib_book`.
  - `title`(40자), `price`
  - `author` : `@ManyToOne(fetch=LAZY)` + `@JoinColumn(name="author_id", nullable=false)`
  - `createdAt`(`@CreatedDate`, 수정 불가), `updatedAt`(`@LastModifiedDate`) – `@EntityListeners(AuditingEntityListener.class)`
  - `changePrice`, `setAuthor`

### 리포지토리 (`Repository`)
- **`AuthorRepository`** – `findByName`, `existsByName` (쿼리 메서드)
- **`BookRepository`** – `findByAuthorId`, `findByTitle`, `countByAuthorId`, `existsByTitleAndAuthorId`

### 서비스 (`Service`)
- **`AuthorService`** – 저장/전체조회/ID조회/이름조회/개수. 못 찾으면 `null` 반환.
- **`BookService`** – 기본 CRUD 위임 + 트랜잭션 메서드:
  - `register(title, price, authorName)` : 제목 공백·길이(정책), 가격 0 이상, 저자 존재, 중복 등록 검증 후 `author.addBook` 으로 저장
  - `changePrice(id, newPrice)` : 가격 변경 (더티 체킹 + save)
  - `delete(id)` : 책 삭제
  - 검증 실패 시 `IllegalArgumentException`

### 러너 (`Runner`)
- **`LibrarySeedRunner`** – 시작 시 시드 적재. 한강(소년이 온다, 채식주의자), 김영하(살인자의 기억법), 정유정(cascade 로 책 2권 동시 저장), 박신인(책 없음). 중복이면 skip.
- **`LibraryReadyRunner`** – 정책 빈 주입 확인 후 준비 완료 로그 출력.

### 컨트롤러 (`Controller`) – 모두 `@GetMapping` 기반, 문자열 반환

| 클래스 | 경로 | 설명 |
|---|---|---|
| `LibraryInfoController` | `/library/hello` | 도서관 이름과 max-title 출력 |
| | `/library/policy/same` | 주입된 두 `LibraryPolicy` 가 같은 인스턴스(싱글톤)인지 확인 |
| `AuthorController` | `/library/author` | 전체 저자 목록(id, 이름, 국가, 책 수) |
| | `/library/authors/sample` | id=1 저자 조회 |
| | `/library/authors/hangang/books` | 한강의 책 목록 + 개수 |
| | `/library/authors/newbie/books` | 박신인(책 없음) 케이스 |
| | `/library/authors/ghost/books` | 존재하지 않는 저자 케이스(`missing`) |
| `BookController` | `/library/books` | 전체 책 목록 |
| | `/library/books/sample` | id=1 책 조회 |
| | `/library/books/by-hangang` | 한강의 책 (`findByAuthorId`) |
| | `/library/books/load-check` | LAZY 로딩 시점 로그 확인 |
| | `/library/books/register` | "흰"(12000원, 한강) 등록 |
| | `/library/books/price-up` | id=1 책 가격 +1000, `createdAt` 유지/`updatedAt` 변경 확인 |
| | `/library/books/delete` | id=1 책 삭제 |

## 사용된 주요 애너테이션

| 애너테이션 | 위치 | 역할 |
|---|---|---|
| `@SpringBootApplication` | Application | 자동 설정 + 컴포넌트 스캔 + 설정 클래스 |
| `@Configuration` / `@Bean` | LibraryConfig | 수동 빈 등록 |
| `@Component` | Runner | 일반 빈 등록 (`ApplicationRunner` 구현) |
| `@Service` | Service | 서비스 계층 빈 |
| `@RestController` | Controller | `@Controller` + `@ResponseBody` |
| `@GetMapping` | Controller | GET 요청 매핑 |
| `@Transactional` | BookService | 트랜잭션 경계 (변경 감지 포함) |
| `@Entity` / `@Table` | Entity | JPA 엔티티 / 테이블명 지정 |
| `@Id` / `@GeneratedValue(IDENTITY)` | Entity | PK, DB 자동 증가 |
| `@Column` | Entity | 컬럼 제약(nullable, unique, length 등) |
| `@OneToMany` / `@ManyToOne` / `@JoinColumn` | Entity | 1:N 양방향 연관관계 (FK = `author_id`) |
| `@EntityListeners(AuditingEntityListener)` | Book | Auditing 리스너 등록 |
| `@CreatedDate` / `@LastModifiedDate` | Book | 생성/수정 시각 자동 기록 |
| `@Getter` / `@RequiredArgsConstructor` | Lombok | getter / final 필드 생성자 자동 생성 |

## 알려진 이슈 / 참고사항

- `BookController.boos()` 는 `boos().isEmpty()` 로 자기 자신을 재귀 호출하므로 `/library/books` 호출 시 `StackOverflowError` 가 발생합니다 (`books.isEmpty()` 가 의도로 보임).
- `Book` 의 Auditing 이 동작하려면 설정 클래스에 `@EnableJpaAuditing` 이 필요합니다. 현재는 선언되어 있지 않아 `createdAt`/`updatedAt` 이 `null` 일 수 있습니다.
- `LibraryInfoController` 는 `LibraryPolicy` 를 두 번 주입받으며 둘 다 같은 싱글톤 빈입니다.
- 컨트롤러 일부는 `open-in-view` 기본값(true) 덕분에 LAZY 로딩이 동작합니다.
- 시드 데이터의 "소년이 온다" 가격이 150000 으로 입력되어 있습니다 (오타 가능성).
