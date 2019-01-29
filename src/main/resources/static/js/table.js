window.onload = fillTable(document.querySelector('#dataTable'), 5, 5); //build default table 5x5

//build table function
function fillTable(table, rows, cols, csvData) {
    document.querySelector('#dataTable').innerHTML = '';//purge table b4 build new
    var row;
    var col = 0;
    if (csvData) var data = csvData.split("\n");       //if data exists begin split into cell values
    (rows) ? row = rows : row = data.length;                    //pick row value for overload method usage
    for (var i = 0; i < row; i++) {                             //start build table
        if (data != null) var cellData = data[i].split(";");  //if data exists split into rows
        var tr = document.createElement('tr');                //create row
        (cols) ? col = cols : col = cellData.length;                   //pick column value for overload method usage
        for (var j = 0; j < col; j++) {                                //begin build columns
            var cellId = String.fromCharCode(97 + j) + (i + 1); //get char code & generate cell id
            var td = document.createElement('td');            //create td element
            var input = document.createElement('input');      //create input element
            td.appendChild(input);                                     //append input into td
            input.type = 'text';                                       //set input type
            input.setAttribute('id', cellId);             //set id for cells
            input.setAttribute('placeholder', cellId);    //set placeholder for cells
            input.setAttribute('onkeyup', 'saveCell(this)'); //set on key up function for save data in cells
            (cellData) ? input.setAttribute('value', cellData[j]) : input.setAttribute('value', getSavedCell(cellId)); //fill cells with data if there is any
            tr.appendChild(td); //append td in tr elements
        }
        table.appendChild(tr);  //build result table
    }
}

//save sell value into local storage
function saveCell(e) {
    var id = e.id;                          //get id property to save cell
    var val = e.value;                      //get the cell value
    localStorage.setItem(id, val);          //set cell value into storage
}

//get cell from local storage
function getSavedCell(v) {
    if (localStorage.getItem(v) === null) {
        return "";                          //set default value
    }
    return localStorage.getItem(v);
}


function uploadCsv() {
    var fileUpload = document.getElementById("csvFile");  //get file from element
    var regex = /(.csv)$/;                                          //check extension
    if (regex.test(fileUpload.value.toLowerCase())) {               //if extension is valid we begin read data
        if (typeof (FileReader) != "undefined") {                   //check data consistency
            var reader = new FileReader();                          //create reader object
            reader.onload = function (e) {                          //set onload function
                fillTable(document.querySelector('#dataTable'), null, null, e.target.result); //build table with file data
            };
            reader.readAsText(fileUpload.files[0]);                 //read bad data if there is any
        } else {
            alert("Используйте браузер с поддержкой HTML5");        //custom exception handle
        }
    } else {
        alert("Просьба использовать файл с расширением *.csv");     //custom exception handle
    }
}


//call result table
function calculate() {
    var arr = tableToArray(document.getElementById('dataTable'));   //get element

    $.ajax({                                                //use ajax to call backend methods
        url: "/calculate",
        method: 'post',
        data: {
            arr: JSON.stringify(arr)
        },
        success: function (resultArr) {
            console.log(resultArr);
        },
        error: function () {
            console.log("fail");

        }
    });
}

//save cell contains into double dimensional array
function tableToArray(table, filename) {
    var result = [];                                                //declare result array variable
    var rows = table.rows;                                          //get table rows
    var cells, t;                                                   //declare variables
    for (var i = 0, iLen = rows.length; i < iLen; i++) {            //iterate over rows
        cells = rows[i].cells;                                      //get columns
        t = [];
        for (var j = 0, jLen = cells.length; j < jLen; j++) {       //iterate over cols
            t.push(cells[j].childNodes[0].value);                   //write data into child nodes (inputs)
        }
        (filename) ? result.push(t.join(";")) : result.push(t);     //if we need to download file - use append with delimiter
    }
    //log result for debug
    if (filename) downloadCsv(result.join("\n"), filename);        //if we need to download file use downloadCsv call
    return result;
}


//download result table function
function downloadCsv(csv, filename) {
    var csvFile;
    var downloadLink;
    csvFile = new Blob([csv], {type: "text/csv"}); //create entity of csv file
    downloadLink = document.createElement("a");            //create download link
    downloadLink.download = filename;                               //set file name
    downloadLink.href = window.URL.createObjectURL(csvFile);        //create a link to the file
    downloadLink.style.display = "none";                            // the link must be not displayed
    document.body.appendChild(downloadLink);                        // add the link to DOM
    downloadLink.click();                                           //export
}

//add event listener to exact id
document.getElementById("downloadCsv").addEventListener("click", function () {
    tableToArray(document.getElementById("dataTable"), "table.csv");
});
