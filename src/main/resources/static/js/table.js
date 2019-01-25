function fillTable(table, row, col) {
    document.querySelector('#dataTable').innerHTML = "";
    for (var i = 0; i < row; i++) {
        var tr = document.createElement('tr');
        for (var j = 0; j < col; j++) {
            var td = document.createElement('td');
            td.innerHTML = 'data ';
            tr.appendChild(td);
        }
        table.appendChild(tr);
    }
}
