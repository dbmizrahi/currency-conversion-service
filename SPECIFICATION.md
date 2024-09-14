## **Currency Conversion Service Documentation**

### **Overview**

The Currency Conversion Service provides an API to convert between different currencies, including both FIAT and cryptocurrencies. It validates requests using API keys, enforces rate limits based on the day of the week, and returns the exchange rates for the specified currencies.

### **Key Features**
- **API Key Authentication**: Every request to the service must include a valid API key.
- **Rate Limiting**:
    - Weekday limit: 100 requests per day.
    - Weekend limit: 200 requests per day.
- **Supported Currencies**: FIAT (USD, EUR) and cryptocurrencies (BTC, ETH).
- **Secure**: Uses Spring Security for request authentication and authorization.

---

### **API Endpoints**

#### 1. **User Registration**

This endpoint allows a new user to register and receive their unique API key.

- **URL**: `/public/register`
- **Method**: `POST`
- **Parameters**:
    - `email`: The email of the user (as a query parameter).

- **Response**: Returns a unique API key for the user.

**Example Request:**
```bash
curl -X POST "http://localhost:8080/public/register?email=user@example.com"
```

**Example Response:**
```json
"your_generated_api_key_here"
```

---

#### 2. **Currency Conversion**

This endpoint returns the exchange rate between two currencies.

- **URL**: `/api/exchange-rates`
- **Method**: `GET`
- **Headers**:
    - `X-Api-Key`: Your API key for authentication.
- **Parameters**:
    - `from`: The source currency (e.g., `BTC`, `USD`).
    - `to`: The target currency (e.g., `ETH`, `EUR`).
    - `amount`: The amount to be converted.

- **Response**: Returns the conversion result in JSON format.

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/exchange-rates?from=BTC&to=USD&amount=100" \
     -H "X-Api-Key: your_api_key"
```

**Example Response:**
```json
{
  "from": "BTC",
  "to": "USD",
  "amount": 999.20,
  "result": 59795460.33200
}
```

---

### **Rate Limiting**

The service applies different rate limits depending on the day of the week:
- **Weekdays** (Monday to Friday): 100 requests per day.
- **Weekends** (Saturday and Sunday): 200 requests per day.

If the request limit is exceeded, the service responds with an error.

**Example Response when Rate Limit Exceeded:**
```json
{
    "error": "Request limit exceeded. Request count: 101"
}
```

---

### **API Key Authentication**

All non-public endpoints require an API key. The API key should be sent in the `X-Api-Key` header of every request. If the API key is missing or invalid, the server will respond with a `401 Unauthorized` error.

**Example of Invalid API Key Response:**
```json
{
    "error": "Invalid or missing API key"
}
```

### **Error Handling**

The service returns appropriate HTTP status codes and error messages when something goes wrong.

- **401 Unauthorized**: Missing or invalid API key.
- **403 Forbidden**: Authentication required but not provided.
- **410 Gone**: Request limit exceeded for the day.

---

### **Setup Instructions for Developers**

1. **Clone the Repository**:
   ```bash
   git clone 
   cd currency-conversion-service
   ```

2. **Configure MongoDB**:
   Ensure that your MongoDB is running and set up correctly. You can configure the connection string in the `application.yml` or `application.properties` file:

   **Example `application.yml`**:
   ```yaml
   spring:
     data:
       mongodb:
         uri: mongodb://<username>:<password>@<host>:<port>/<database>
   ```

3. **Configure Rate Limits**:
   You can configure the rate limits in `application.yml`:

   ```yaml
   request:
     limit:
       weekday: 100
       weekend: 200
   ```

4. **Run the Application**:
   Use Gradle to build and run the application:

   ```bash
   ./gradlew bootRun
   ```

---

### **Security Configuration**

This service uses **Spring Security** for authentication and request validation. The key aspects of security are:
- **API Key-based Authentication**: Implemented via a custom `ApiKeyFilter` that checks the `X-Api-Key` header.
- **Custom Authentication Logic**: The `ApiKeyFilter` is responsible for validating the API key and enforcing rate limits.

---

### **Dependencies**
- **Spring Boot**: 3.x
- **Spring Security**: 6.x
- **Spring Data MongoDB**: 4.x
- **Lombok**: For reducing boilerplate code
- **Jackson**: For JSON processing
- **Gradle**: For build management

---

### **Project Structure**
- **`/config`**: Contains configuration classes like `ApiKeyFilter` and `SecurityConfig`.
- **`/controllers`**: REST controllers for handling API requests.
- **`/repositories`**: MongoDB repositories for interacting with the database.
- **`/entities`**: Contains MongoDB entity classes, such as `UserKey` and `ApiRequestLog`.
- **`/services`**: Service layer for business logic (if applicable).
