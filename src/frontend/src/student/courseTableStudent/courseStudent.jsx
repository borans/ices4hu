import React from "react";

function CourseTable_Student(props) {

const {id, department, code, name, courseType, credit, courseDegree, instructorName, evaluationFormStatus} = props;

return(
<tr>
    <td>{code}</td>
    <td>{name}</td>
    <td>{instructorName}</td>
    <td>{department}</td>
    <td>{courseType}</td>
    <td>{courseDegree}</td>
    <td>{credit}</td>
    <td>{evaluationFormStatus}</td>
</tr>
)
}

export default CourseTable_Student;