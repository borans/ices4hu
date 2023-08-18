import React from "react";

function CourseTable_Instructor(props) {

const {id, department, code, name, courseType, credit, courseDegree} = props;

return(
<tr>
    <td>{code}</td>
    <td>{name}</td>
    <td>{department}</td>
    <td>{courseType}</td>
    <td>{courseDegree}</td>
    <td>{credit}</td>
</tr>
)
}

export default CourseTable_Instructor;