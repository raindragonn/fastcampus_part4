# fastcampus_part4

## chapter01_youtube

> 유튜브 영상 플레이어 화면전환 애니메이션 구현, ExoPlayer를 이용한 영상재생

**사용 기술**

 - `MotionLayout`
 
    - 레이아웃 전환과 UI 이동, 크기 조절 및 애니메이션에 사용 됩니다. 

    - ConstraintLayout 라이브러리의 일부 입니다.(서브 클래스)

 - `ExoPlayer`

    - Google이 Android SDK 와 별도로 배포되는 오픈소스 프로젝트

    - 오디오 및 동영상 재생

    - [link](https://exoplayer.dev/hello-world.html)


## chapter02_music

> 재생 목록 화면과 플레이 화면을 가진 Exoplayer를 활용한 뮤직 플레이어 앱

**사용 기술**

- `ExoPlayer`

- `ListAdapter, DiffUtil`

   - 기존의 recyclerView.adapter 사용시 아이템 변경에 대한 처리를 보다 쉽게 이용이 가능합니다.

   - 보일러플레이트 코드를 많이 줄일수 있습니다.

## chapter03_map

> google 지도 기반의 앱 입니다

**사용 기술**

- `Coroutine`

   - 비동기 처리를 위한 솔루션

- `okhttp3, retrofit2`

   - Okhttp 클라이언트를 이용해서 타임아웃설정 및 네트워크 통신정보를 확인해볼 수 있습니다.

- `google map API`

- `Tmap POI API`


## chapter04_ott_intro

> ott앱의 인트로를 따라 변화하는 레이아웃

**사용 기술**

- `MotionLayout`

- `CoordinatorLayout`


## chapter05_githubRepo

> 깃헙 레파지토리를 검색하고 찜목록에 추가해서 리스트를 확인할 수 있습니다.

**사용 기술**

- `Coroutine`

- `github api`

## chapter06_dust

> 오픈 api로부터 미세먼지 정보를 가져와 확인 및 홈스크린에 위젯을 추가할 수 있습니다.

**사용 기술**

-  `FusedLocationProviderClient` 

- `App Widget`

   - `AppwidgetProviderInfo`

      - 앱 위젯에 대한 정보를 담는 xml,  layout size, 업데이트주기,초기에 보여줄 레이아웃이 어떤건지등 정보를 담는다

   - `AppWidgetProvider`

     - 시스템에서 위젯을 제공하는 이벤트가 발생시 이를 수신해서 실제로 제공할 수 있는 객체, 위젯이 갱신, 활성화, 비활성화, 삭제등의 이벤트 핸들링이 가능하다.

- `Coroutine`

## chatper07_imageSearch

> Unsplash API를 이용해 사진을 불러와 다운받을 수 있으며 배경화면으로 설정할 수 있습니다.

**사용 기술**

- `ShimmerLayout`

  - loading shimmer란 페이스북, 인스타그램에서 로딩중에 보이는 UI 틀을 유지하면서 로딩 하는것

   - facebook에서 지원하는 라이브러리 입니다.

   - [http://facebook.github.io/shimmer-android/](http://facebook.github.io/shimmer-android/)

- `WallpaperManager`

   - 배경화면을 관리할 수 있는 클래스

- `Coroutine`

