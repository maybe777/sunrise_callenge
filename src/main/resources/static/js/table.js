window.onload = fillTable(document.querySelector('#dataTable'), 5, 5); //build default table 5x5

//build table function
function fillTable(table, row, col) {
    document.querySelector('#dataTable').innerHTML = '';
    for (var i = 0; i <= row; i++) {
        var tr = document.createElement('tr');
        for (var j = 0; j <= col; j++) {
            var cellId = String.fromCharCode(97 + j - 1) + i; //get char code & generate cell id
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

//save sell value into local storage
function saveCell(e) {
    var id = e.id; //get id property to save cell
    var val = e.value; //get the cell value
    localStorage.setItem(id, val); //set cell value into storage
}

//get cell from local storage
function getSavedCell(v) {
    if (localStorage.getItem(v) === null) {
        return ""; //default value
    }
    return localStorage.getItem(v);
}
