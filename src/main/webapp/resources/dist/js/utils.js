
/*To format date in DDMMYYYY Format*/
function formatDate(dateInput) {
    if (!dateInput) return ""; 
    const date = new Date(dateInput);
    if (isNaN(date)) return ""; 

    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();

    return `${day}/${month}/${year}`;
}

function getTodaysDate(){
	const today = new Date();
	const year = today.getFullYear();
	const month = String(today.getMonth() + 1).padStart(2, '0'); // Months are zero-based
	const day = String(today.getDate()).padStart(2, '0');

	const maxDate = `${year}-${month}-${day}`;
	return maxDate
}