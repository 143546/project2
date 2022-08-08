function render(surveyList) {
    var tbody = document.querySelector('tbody')
    for (var i in surveyList) {
        var survey = surveyList[i]

        var html = `<tr><td>${survey.sid}</td><td>${survey.title}</td><td>${survey.brief}</td></tr>`
        tbody.innerHTML += html
    }
}

window.onload = function() {
    var xhr = new XMLHttpRequest()
    xhr.open('get', '/survey/list.json')
    xhr.onload = function() {
        if (this.status !== 200) {
            alert('请求后端 JSON 出错，先检查 /survey/list.json 的结果吧')
            return
        }

        console.log(this.responseText)
        var data = JSON.parse(this.responseText)
        // TODO: 当前用户的渲染
        render(data.surveyList)
    }
    xhr.send()
}