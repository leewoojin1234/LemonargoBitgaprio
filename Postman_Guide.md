## Postman 사용 가이드

**기본 URL:** `http://localhost:8080` (애플리케이션이 8080 포트에서 실행 중이라고 가정)

---

### 1. 사용자 관리 (User Management)

#### 1.1. 사용자 생성 (Create User)

*   **HTTP Method:** `POST`
*   **URL:** `/api/users`
*   **Headers:**
    *   `Content-Type`: `application/json`
*   **Body (raw, JSON):**
    ```json
    {
        "userId": "your_email@example.com"
    }
    ```
*   **설명:** `userId`는 사용자의 고유 식별자이며 이메일 형식일 필요는 없지만, `NotBlank` 제약조건이 있으므로 비어있으면 안 됩니다. 성공 시 `201 Created`와 생성된 사용자 정보가 반환됩니다. `userId`가 비어있을 경우 `400 Bad Request`가 반환됩니다.

#### 1.2. 사용자 조회 (Find User by UserId)

*   **HTTP Method:** `GET`
*   **URL:** `/api/users/{userId}`
    *   `{userId}` 부분에 조회하고자 하는 사용자의 실제 `userId`를 입력하세요. 예: `/api/users/your_email@example.com`
*   **Headers:** (필요 없음)
*   **Body:** (없음)
*   **설명:** `{userId}`에 해당하는 사용자 정보를 반환합니다. 성공 시 `200 OK`와 사용자 정보가 반환됩니다. 존재하지 않는 `userId`인 경우 `404 Not Found`가 반환됩니다.

#### 1.3. 사용자 정보 수정 (Update User)

*   **HTTP Method:** `PUT`
*   **URL:** `/api/users/{userId}`
    *   `{userId}` 부분에 수정하고자 하는 사용자의 실제 `userId`를 입력하세요. 예: `/api/users/your_email@example.com`
*   **Headers:**
    *   `Content-Type`: `application/json`
*   **Body (raw, JSON):**
    ```json
    {
        "userId": "new_email@example.com",
        "currentMoney": 20000,
        "totalDebt": 900000
    }
    ```
*   **설명:** `{userId}`에 해당하는 사용자 정보를 수정합니다. `currentMoney`와 `totalDebt`는 0 이상이어야 합니다. `userId` 필드는 필수가 아니므로 원하는 필드만 넣어 수정할 수 있습니다. 성공 시 `200 OK`와 업데이트된 사용자 정보가 반환됩니다. 존재하지 않는 `userId`인 경우 `404 Not Found`가, 유효하지 않은 데이터인 경우 `400 Bad Request`가 반환됩니다.

#### 1.4. 사용자 삭제 (Delete User)

*   **HTTP Method:** `DELETE`
*   **URL:** `/api/users/{userId}`
    *   `{userId}` 부분에 삭제하고자 하는 사용자의 실제 `userId`를 입력하세요. 예: `/api/users/your_email@example.com`
*   **Headers:** (필요 없음)
*   **Body:** (없음)
*   **설명:** `{userId}`에 해당하는 사용자를 삭제합니다. 성공 시 `204 No Content`가 반환됩니다. 존재하지 않는 `userId`인 경우 `404 Not Found`가 반환됩니다.

---

### 2. 빚 관리 (Debt Management)

#### 2.1. 빚 상환 (Repay Debt)

*   **HTTP Method:** `PUT`
*   **URL:** `/api/debts/{userId}/repay`
    *   `{userId}` 부분에 빚을 상환할 사용자의 실제 `userId`를 입력하세요. 예: `/api/debts/your_email@example.com/repay`
*   **Headers:**
    *   `Content-Type`: `application/json`
*   **Body (raw, JSON):**
    ```json
    {
        "amount": 10000
    }
    ```
*   **설명:** `{userId}`에 해당하는 사용자의 빚을 `amount`만큼 상환합니다. `amount`는 0보다 커야 합니다. 성공 시 `200 OK`와 빚 상환 내역 정보가 반환됩니다. 존재하지 않는 `userId`인 경우 `404 Not Found`가, 잔액 부족 등 비즈니스 로직에 위배되는 경우 `400 Bad Request`가 반환될 수 있습니다. (예: "Not enough money to repay debt.")

#### 2.2. 빚 상환 내역 조회 (Get Debt History)

*   **HTTP Method:** `GET`
*   **URL:** `/api/debts/{userId}/history`
    *   `{userId}` 부분에 빚 상환 내역을 조회할 사용자의 실제 `userId`를 입력하세요. 예: `/api/debts/your_email@example.com/history`
*   **Headers:** (필요 없음)
*   **Body:** (없음)
*   **설명:** `{userId}`에 해당하는 사용자의 모든 빚 상환 내역을 반환합니다. 성공 시 `200 OK`와 빚 상환 내역 리스트가 반환됩니다. 존재하지 않는 `userId`인 경우 `404 Not Found`가 반환됩니다.