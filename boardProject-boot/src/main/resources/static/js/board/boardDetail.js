/* 좋아요 버튼(하트) 클릭 시 비동기로 좋아요 INSERT/DELETE */

// Thymeleaf 코드 해석 순서
// 1. th : 코드(java) + Spring EL 
// 2. html 코드(+ css, js)

// 1) 로그인한 회원 번호 준비
//    --> session에서 얻어오기
//        (session은 서버에서 관리하기 때문에 JS에서 바로 얻어올 방법은 없음)
// 2) 현재 게시글 번호 준비
// 3) 좋아요 여부 준비

// 1. #boardLike가 클릭되었을 때
document.querySelector("#boardLike").addEventListener("click", e => {

    // 2. 로그인 상태가 아닌 경우 동작 X
    if(loginMemberNo == null) {
        alert("로그인 후 이용해주세요");
        return;
    }

    // 3. 준비된 3개의 변수를 객체로 저장(JSON 변환 예정)
    const obj = {
        "memberNo" : loginMemberNo,
        "boardNo" : boardNo,
        "likeCheck" : likeCheck
    };

    // 4. 좋아요 INSERT/DELETE 비동기 요청
    fetch("/board/like", {
        method : "POST",
        headers : {"Content-Type" : "application/json"},
        body : JSON.stringify(obj)
    })
    .then(resp => resp.text()) // 반환 결과 text 형태로 변환
    .then(count => {
        if(count == -1) {
            console.log("좋아요 처리 실패");
            return;
        }

        // 5. likeCheck 값 0 <-> 1 변환
        // -> 클릭될 때 마다 INSERT/DELETE 동작을 번갈아 가면서 하게끔...
        likeCheck = likeCheck == 0 ? 1 : 0;

        // 6. 하트를 채웠다/비웠다 바꾸기
        e.target.classList.toggle("fa-regular");
        e.target.classList.toggle("fa-solid");

        // 7. 게시글 좋아요 수 수정
        e.target.nextElementSibling.innerText = count;
    })
});

// --------------------- 게시글 수정 버튼 ------------------

const updateBtn = document.querySelector("#updateBtn");

if(updateBtn != null) { // 수정 버튼 존재 시

    updateBtn.addEventListener("click", () => {

        // GET 방식
        // 현재 : /board/1/2003?cp=1
        // 목표 : /editBoard/1/2001/update
        location.href = location.pathname.replace('board', 'editBoard')
                        + "/update"
                        + location.search;
    });

}