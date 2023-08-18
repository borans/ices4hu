import React from "react";

function Notifications_Table(props) {

const {surveyType, courseCode, courseName} = props;

return(
<tr>
    <td>{surveyType}</td>
    <td>{courseCode}</td>
    <td>{courseName}</td>
</tr>
)
}

export default Notifications_Table;