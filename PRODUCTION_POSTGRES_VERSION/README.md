# 🚗 BlaBlaCar PRODUCTION (Version B)

## PostgreSQL + Email OTP + Render.com Ready

### Prerequisites
- PostgreSQL database (local or Render.com)
- Gmail account (for OTP)
- GitHub account (for Render deployment)

### Local Development
```bash
# Configure PostgreSQL connection
export DATABASE_URL=jdbc:postgresql://localhost:5432/blablacar
export DB_USERNAME=postgres
export DB_PASSWORD=your-password

# Build & Run
mvn clean install
mvn spring-boot:run
```

### Production Deployment (Render.com)

1. **Push to GitHub**
2. **Create PostgreSQL on Render**
3. **Create Web Service**
4. **Set Environment Variables:**
   - DATABASE_URL
   - EMAIL_USERNAME
   - EMAIL_PASSWORD
   - SPRING_PROFILES_ACTIVE=prod
5. **Deploy!**

### Email Setup
1. Go: https://myaccount.google.com/apppasswords
2. Generate app password
3. Set EMAIL_PASSWORD env var

### Default Admin
```
Email: admin@blablacar.com
Password: Admin@123
```

Change immediately after first login!

### Features
- ✅ Real email OTP
- ✅ PostgreSQL database
- ✅ Admin panel
- ✅ Production security
- ✅ Multi-environment support
