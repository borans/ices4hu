import React from "react";

function Notifications_Table(props) {

const {courseName, instructorName, deadline} = props;

return(
<tr>
    <td>{courseName}</td>
    <td>{instructorName}</td>
    <td>{new Date(deadline).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
</tr>
)
}

export default Notifications_Table;