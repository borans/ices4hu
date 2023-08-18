import React from "react";

function CourseTable_Admin(props) {

const {id, department, code, name, courseType, credit, courseDegree, instructorName, evaluationFormStatus, onEdit, onSave} = props;


const handleRequest = () => {

    onSave({
        course: {
            id: id,
            name: name,
            code: code
        }
    });
    
}


return(
<tr>
    <td>{code}</td>
    <td>{name}</td>
    <td>{department}</td>
    <td>{courseType}</td>
    <td>{courseDegree}</td>
    <td>{credit}</td>
    <td><button onClick={(event) => {event.preventDefault(); onEdit(id, name, credit)}}>EDIT COURSE</button></td>
    <td><button onClick={handleRequest}>REMOVE COURSE</button></td>

</tr>
)
}

export default CourseTable_Admin;