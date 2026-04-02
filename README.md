# 🐿️ Travel App Backend

Travel App 서비스의 백엔드 API 서버입니다.
사용자에게 안정적인 서비스를 제공하기 위해 **오라클 클라우드(OCI)** 환경에서 **GitHub Actions**와 **Docker**, **Nginx**를 활용한 **자동화된 무중단 배포(Blue-Green)** 파이프라인을 구축했습니다.

---

## 🛠️ Tech Stack

**Backend**
- Java 17
- Spring Boot 3.2.4
- Spring Data JPA
- PostgreSQL 15

**Infrastructure & CI/CD**
- Oracle Cloud Infrastructure (OCI) - ARM64 (Ampere)
- Docker & Docker Compose
- Nginx (Reverse Proxy & Load Balancing)
- GitHub Actions

---

## 🚀 Server & API Info

- **Base URL:** `http://daramjwi.com`
- **주요 API 예시:**
    - `GET /api/v1/questions` : 질문 목록 조회 응답

---

## 🏗️ Architecture & Zero-Downtime Deployment

이 프로젝트는 서비스 중단 없는 업데이트를 위해 **Blue-Green 무중단 배포** 방식을 채택했습니다.

1. **CI/CD Pipeline (GitHub Actions)**
    - `main` 브랜치에 코드가 푸시되면 GitHub Actions가 트리거됩니다.
    - OCI ARM64 아키텍처에 호환되도록 `QEMU` 및 `Docker Buildx`를 사용하여 다중 플랫폼(multi-platform) 이미지를 빌드하고 Docker Hub에 푸시합니다.

2. **Blue-Green Deployment**
    - 서버 내의 `deploy.sh` 스크립트가 실행되어 현재 사용하지 않는 포트(예: 8081이 가동 중이면 8082)에 새 버전의 컨테이너를 실행합니다.
    - 새 컨테이너가 정상적으로 띄워졌는지 `Health Check`를 최대 10회 진행합니다.
    - 성공 시, Nginx의 `resolver`와 변수를 통해 트래픽을 새 컨테이너로 스위칭(Reload)합니다.
    - 실패 시, 기존 컨테이너를 유지하고 실패한 컨테이너를 삭제하는 자동 롤백(Rollback) 안전장치가 마련되어 있습니다.

---

## ⚙️ How to Run Locally

### 1. 환경 변수 설정
프로젝트 최상단에 `.env` 파일을 생성하고 아래 정보를 입력합니다.
```env
POSTGRES_USER=your_db_user
POSTGRES_PASSWORD=your_db_password