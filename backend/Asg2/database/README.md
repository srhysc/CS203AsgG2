# Tariff Application Backend

This is the backend service for the **Tariff Application**, built with Spring Boot and MySQL.

---

## 📦 Requirements

- **Java 21** (or higher)
- **Maven** (or use the included `./mvnw` wrapper)
- **MySQL 8.0+**

---

## 🗄️ Database Setup

1. **Create the database** (if it doesn’t exist):
   ```sql
   CREATE DATABASE tariff_app;
   ```

2. **Import schema and data** (from the SQL dump in this repo):
   ```bash
   mysql -u root -p tariff_app < backend/Asg2/db/tariff_app.sql
   ```
   > 💡 You will be prompted for your MySQL password.  
   > Make sure you are using the same user/password configured in `src/main/resources/application.properties`.

3. (Optional) Verify the import:
   ```sql
   USE tariff_app;
   SHOW TABLES;
   SELECT * FROM country_codes LIMIT 5;
   SELECT * FROM vat_rates LIMIT 5;
   ```

---

## ⚙️ Configuration

Update `src/main/resources/application.properties` to match your local database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tariff_app?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password_here
spring.jpa.hibernate.ddl-auto=none
```

> ⚠️ Do **not** commit real production credentials.  
> Use placeholders or environment variables if possible.

---

## 🚀 Run the Application

From the project root, run:

```bash
./mvnw spring-boot:run
```

Or, if you have Maven installed globally:

```bash
mvn spring-boot:run
```

The application will start on [http://localhost:8080](http://localhost:8080).

---

## 🔧 API Endpoints (Quick Reference)

- **GET** `/countries` – Fetch all countries + VAT rates  
- **GET** `/trade-agreements` – Fetch all trade agreements  

> See `CountryController.java` and `TradeAgreementController.java` for full API logic.

---

## 🤝 Contribution Workflow

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```
2. **Commit your changes**
   ```bash
   git commit -m "Describe your change"
   ```
3. **Push to remote**
   ```bash
   git push origin feature/your-feature-name
   ```
4. **Open a pull request**

---

## 📝 Notes

- When you modify the database schema or seed data:
  1. Re-export with:
     ```bash
     mysqldump -u root -p tariff_app > backend/Asg2/db/tariff_app.sql
     ```
  2. Commit the updated `.sql` file so others can stay in sync.

- Consider adding `.vscode/settings.json` to `.gitignore` to avoid committing editor-specific settings.

---

## 📄 License

MIT License (add your license file if applicable)
