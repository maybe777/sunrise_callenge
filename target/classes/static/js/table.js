window.onload = fillTable(document.querySelector('#dataTable'), 5, 5);

function fillTable(table, row, col) {
    document.querySelector('#dataTable').innerHTML = '';
    for (var i = 0; i <= row; i++) {
        var tr = document.createElement('tr');
        for (var j = 0; j <= col; j++) {
            var cellId = String.fromCharCode(97 + j - 1) + i;
            var td = document.createElement('td');
            var input = document.createElement('input');
            (j === 0 || i === 0) ? td.innerHTML = String.fromCharCode(97 + j - 1) : td.appendChild(input);
            if (j === 0) td.innerHTML = i;
            if (i === 0 && j === 0) td.innerHTML = '';
            input.type = 'text';
            input.setAttribute('id', cellId);
            input.setAttribute('onkeyup', 'saveCell(this)');
            input.setAttribute('value', getSavedCell(cellId));
            tr.appendChild(td);
        }
        table.appendChild(tr);
    }
}

function saveCell(e) {
    var id = e.id;  // get the sender's id to save it .
    var val = e.value; // get the value.
    localStorage.setItem(id, val);// Every time user writing something, the localStorage's value will override .
}

//get the saved value function - return the value of "v" from localStorage.
function getSavedCell(v) {
    if (localStorage.getItem(v) === null) {
        return "";// You can change this to your defualt value.
    }
    return localStorage.getItem(v);
}
