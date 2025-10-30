# CleanBin - 분리수거 도우미 앱 🌱

분리수거 방법을 쉽고 빠르게 알려주는 Android 앱입니다.

## 주요 기능

- 📷 **사진 촬영**: 카메라로 직접 물건을 촬영하여 분석
- 🖼️ **앨범 선택**: 갤러리에서 이미지를 선택하여 분석
- ♻️ **분리수거 방법 안내**: 상세한 분리수거 방법 제공
- 💡 **유용한 팁**: 재활용률을 높이는 팁 제공
- 📸 **참고 이미지**: 단계별 참고 이미지 제공

## 기술 스택

- **언어**: Kotlin
- **UI**: Jetpack Compose
- **아키텍처**: MVVM (Model-View-ViewModel)
- **비동기 처리**: Kotlin Coroutines + Flow
- **네비게이션**: Jetpack Navigation Compose
- **이미지 로딩**: Coil
- **네트워크**: Retrofit2 (예정)
- **최소 SDK**: 26 (Android 8.0)

## 프로젝트 구조

```
app/src/main/java/net/cleanbin/myapplication/
├── data/
│   ├── api/
│   │   └── RecyclingApiService.kt      # API 인터페이스
│   ├── model/
│   │   ├── RecyclingCategory.kt        # 데이터 모델
│   │   └── UiState.kt                  # UI 상태 관리
│   └── repository/
│       └── RecyclingRepository.kt      # 데이터 레포지토리
├── ui/
│   ├── screen/
│   │   ├── HomeScreen.kt               # 메인 화면
│   │   └── ResultScreen.kt             # 결과 화면
│   ├── theme/                          # 테마 설정
│   └── viewmodel/
│       └── RecyclingViewModel.kt       # ViewModel
├── navigation/
│   ├── Screen.kt                       # 화면 경로 정의
│   └── NavGraph.kt                     # 네비게이션 그래프
└── MainActivity.kt                     # 메인 액티비티
```

## Google 권장 아키텍처

이 프로젝트는 Google의 Android 앱 아키텍처 가이드를 따릅니다:

### 계층 구조

1. **UI Layer** (ui/)
   - Composable 화면들
   - ViewModel을 통한 상태 관리
   - 단방향 데이터 흐름 (UDF)

2. **Domain Layer** (domain/) - 필요시 추가 예정
   - 비즈니스 로직
   - Use Cases

3. **Data Layer** (data/)
   - Repository: 데이터 소스 추상화
   - API Service: 네트워크 통신
   - Model: 데이터 모델

### MVVM 패턴

- **Model**: 데이터와 비즈니스 로직 (Repository, API)
- **View**: UI (Composable functions)
- **ViewModel**: UI 상태 관리 및 비즈니스 로직 호출

### 상태 관리

- `StateFlow`를 사용한 반응형 상태 관리
- `UiState` sealed class로 로딩/성공/실패 상태 표현

## 분리수거 카테고리

- 📄 종이류 (Paper)
- 🔵 플라스틱 (Plastic)
- 🟠 유리 (Glass)
- 🔴 캔/금속 (Can)
- 🟣 비닐 (Vinyl)
- 🔷 스티로폼 (Styrofoam)
- ⚫ 일반쓰레기 (General)
- 🟢 음식물 (Food)

## 개발 예정 사항

- [ ] 카메라 촬영 기능 구현
- [ ] 실제 API 서버 연동
- [ ] 이미지 분석 결과 캐싱
- [ ] 분리수거 히스토리 기능
- [ ] 지역별 분리수거 규정 안내
- [ ] 다크 모드 지원

## 빌드 및 실행

```bash
# 클론
git clone https://github.com/SUMTECH-HACKATON/android.git
cd android

# 빌드
./gradlew build

# 실행 (Android Studio에서 Run 또는)
./gradlew installDebug
```

## 라이선스

Copyright © 2024 CleanBin Team
