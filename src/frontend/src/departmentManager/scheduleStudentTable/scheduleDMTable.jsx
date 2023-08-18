import React from "react";

function ScheduleTable_DM(props) {

const {startingTime, endDate, gradeFinalizationDate} = props;

return(
<tr>
    <td>{startingTime===null ? "NOT SET YET!" : new Date(startingTime).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
    <td>{endDate===null ? "NOT SET YET!" : new Date(endDate).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
    <td>{gradeFinalizationDate===null ? "NOT SET YET!" : new Date(gradeFinalizationDate).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
</tr>
)
}

export default ScheduleTable_DM;