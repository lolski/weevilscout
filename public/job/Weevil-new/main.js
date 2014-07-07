var xhr = new XMLHttpRequest();
xhr.onreadystatechange = ready;
xhr.open("GET", "redundant.js", false);
xhr.send();

function ready() {
    if (xhr.readyState !== 4) {
        return false;
    }
    if (xhr.status !== 200) {
        // error
    }

    //submit the job 10 times
    redundant(1, 3);
    //wait for all to finish
}