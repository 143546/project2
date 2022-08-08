function renderWho(user) {
    if (user) {
        document.querySelector('.who').textContent = user.username
    } else {
        document.querySelector('.who').textContent = '需要登录后才能使用'
    }
}

function renderPagination(pagination) {
    var currentPage = parseInt(pagination.currentPage)
    var totalPage = parseInt(pagination.totalPage)
    if (currentPage === 1) {
        document.querySelector('#prevPage').href += 1
    } else {
        document.querySelector('#prevPage').href += (currentPage - 1)
    }

    document.querySelector('#countPerPage').textContent = pagination.countPerPage
    document.querySelector('#currentPage').textContent = currentPage
    document.querySelector('#totalPage').textContent = totalPage

    if (currentPage >= totalPage) {
        document.querySelector('#nextPage').href += totalPage
    } else {
        document.querySelector('#nextPage').href += (currentPage + 1)
    }

    document.querySelector('#lastPage').href += totalPage
}

function render(questionList) {
    var tbody = document.querySelector('tbody')
    for (var i in questionList) {
        var question = questionList[i]
        var html = `<tr><td>${question.qid}</td><td>${question.question}</td>`
        for (var j in question.options) {
            var option = question.options[j]
            html += `<td>${option}</td>`
        }
        html += `<td>${question.refCount}</td></tr>`

        tbody.innerHTML += html
    }
}

// window 的 load
// 当整个页面及所有依赖资源如样式表和图片都已完成加载时，将触发load事件。
window.onload = function() {
    // 1. 发起 ajax 请求
    var xhr = new XMLHttpRequest()
    xhr.open('get', '/question/list.json' + location.search)
//    xhr.open('get', './list.json')
    xhr.onload = function() {
        // 2. 根据得到的响应，进行结果的渲染
        if (xhr.status !== 200) {
            alert('访问 /question/list.json 出错了，优先调试下这个 URL')
            return
        }

        console.log(xhr.responseText)
        var data = JSON.parse(xhr.responseText)

        renderWho(data.currentUser)
        if (data.pagination) {
            renderPagination(data.pagination)
        }
        if (data.questionList) {
            render(data.questionList)
        }
    }
    xhr.send()
}