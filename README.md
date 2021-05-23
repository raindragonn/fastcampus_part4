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

- `okhttp3, retrofit2`

- `google map API`

- `POI API`



