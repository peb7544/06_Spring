//console.log("메인js");

// 쿠키에서 key가 일치하는 value 얻어오기 함수

// 쿠키는 "K=V; K=V; K=V..." 형식

// 배열.map(함수) : 배열의 각 요소를 이용해 함수 수행 후
//                 결과 값으로 새로운 배열을 만들어서 반환
const getCookie = (key) => {

    const cookies = document.cookie;

    // cookies 문자열을 배열 형태로 변환
    const cookieList = cookies.split("; ") // ["K:V", "K:V"]
                                .map( el => el.split("=") );

    // 배열 -> 객체로 변환(그래야 다루기 쉽다)
    const obj = {}; // 비어있는 객체 선언

    for(let i=0; i < cookieList.length; i++) {
        const k = cookieList[i][0]; // key 값
        const v = cookieList[i][1]; // value 값
        obj[k] = v;
    }

    //console.log(obj);

    return obj[key]; // 매개변수로 전달받은 key와
    //                  obj 객체에 저장된 키와 일치하는 요소의 value 값 반환
    
}

const loginEmail = document.querySelector("#loginForm input[name='memberEmail']");

// 로그인 안된 상태인 경우에 수행
if(loginEmail != null) { // 로그인창의 이메일 입력부분이 화면에 있을 때

    // 쿠키 중 key 값이 "saveId"인 요소의 value 얻어오기
    const saveId = getCookie("saveId"); // undefined 또는 이메일

    // saveId 값이 있을 경우
    if(saveId != undefined) {
        loginEmail.value = saveId;

        // 아이디 저장 체크박스에 체크 해두기
        document.querySelector("input[name='saveId']").checked = true;
    }
};

// 이메일, 비밀번호 미작성 시 로그인 막기
const loginForm = document.querySelector("#loginForm");

const loginPw = document.querySelector("#loginForm input[name='memberPw']");

// #loginForm이 화면에 존재할 때(== 로그인 상태 아닐 때)
if(loginForm != null) {

    // 제출 이벤트 발생 시 
    loginForm.addEventListener("submit", e=> {

        // 이메일 미작성
        if(loginEmail.value.trim().length === 0) {
            alert("이메일을 작성해주세요!");
            e.preventDefault(); // 기본 이벤트(제출) 막기
            loginEmail.focus(); // 초점 이동
            return;
        }

        // 비밀번호 미작성
        if(loginPw.value.trim().length === 0) {
            alert("비밀번호를 작성해주세요!");
            e.preventDefault(); // 기본 이벤트(제출) 막기
            loginPw.focus(); // 초점 이동
            return;
        }
    });
}


// ---------------------------------------------------------------------

/* 빠른 로그인 */

const qickLoginBtns = document.querySelectorAll(".qick-login");

qickLoginBtns.forEach((item, index) => {
    // item : 현재 반복 시 꺼내욘 객체
    // index : 현재 반복 중인 인덱스

    // qickLoginBtns 요소인 button 태그 하나씩 꺼내서 이벤트 리스너 추가
    item.addEventListener("click", () =>{
        const email = item.innerText; // 버튼에 작성된 이메일 얻어오기

        location.href = "/member/quickLogin?memberEmail=" + email;
    });
});

// ---------------------------------------------------------------------

/* 회원 목록 조회(비동기) */

const selectMemberList = document.querySelector("#selectMemberList");
const memberList = document.querySelector("#memberList");

selectMemberList.addEventListener("click", () => {
    
    fetch("/member/selectMemberList")
    .then(resp => resp.text())
    .then(result => {
        console.log(JSON.parse(result))

        const mList = JSON.parse(result);

        for(let member of mList) {

            const tr = document.createElement("tr");
            const arr = ['memberNo', 'memberEmail', 'memberNickname', 'memberDelFl'];

            for(let key of arr) {
                const td = document.createElement("td");

                td.innerText = member[key];
                tr.append(td);
                
            }

            // tbody의 자식으로 tr(한 행) 추가
            memberList.append(tr);
        }

    });
});

// ---------------------------------------------------------------------

/* 특정 회원 비밀번호 초기화(Ajax) */
const resetMemberNo = document.querySelector("#resetMemberNo");
const resetPw = document.querySelector("#resetPw");

resetPw.addEventListener("click", () => {

    // 입력받은 회원번호 얻어오기
    const inputNo = resetMemberNo.value;

    if(inputNo.trim().length == 0) {
        alert("회원번호 입력해주세요.");
        return;
    }

    //fetch("/member/resetPw?memberNo=" + inputResetMemberNo)
    fetch("/member/resetPw", {
        method : "PUT", // PUT : 수정 요청 방식
        headers : {"Content-Type" : "application/json"},
        body : inputNo
    })
    .then(resp => resp.text())
    .then(result => {
        // result == 컨트롤러부터 반환받아 TEXT로 파싱한 값
        if(result > 0) alert("초기화 성공");
        else alert("초기화 실패");
    });
});


// ---------------------------------------------------------------------

/* 특정 회원(회원번호) 탈퇴 복구(Ajax) */
const restorationMemberNo = document.querySelector("#restorationMemberNo");
const restorationBtn = document.querySelector("#restorationBtn");

restorationBtn.addEventListener("click", () => {

    const inputMemberNo = restorationMemberNo.value

    fetch("/member/restoration?memberNo=" + inputMemberNo)
    .then(resp => resp.text())
    .then(result => {
        if(result > 0) {
            alert("복구 성공");
            restorationMemberNo.value = "";
        } else alert("복구 실패");
    });
});