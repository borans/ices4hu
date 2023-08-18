import React, { useEffect, useState } from "react";
import styles from "../../components/courses.module.css";

function CourseTable_Manager(props) {
  const {
    id,
    department,
    code,
    name,
    courseType,
    credit,
    courseDegree,
    instructorName,
    instructors,
    onSave
  } = props;
 

  const [selectedInstructor, setSelectedInstructor] = useState("");
  const [def, setDefault] = useState(instructorName);
  const [pairs, setPairs] = useState([]);
  const [isSavedBefore, setIsSavedBefore] = useState(false);
  const nameList = [];

  useEffect(() => {
    setSelectedInstructor(instructorName);
    setDefault(instructorName);
    console.log("def:"+ def);
  });

  const handleInstructorChange = (event) => {
    setSelectedInstructor(event.target.value);
    onSave({
      instructor: {
          instructorId: Number(event.target.value),
          courseId: id
      }
  });
  };

  return (
    <tr>
      <td>{id}</td>
      <td>{code}</td>
      <td>{name}</td>
      <td>
        <select onChange={handleInstructorChange}>
          <option value="none" selected disabled hidden>Select an Instructor</option>
          {instructors.map((instructor) => (
            <option key={instructor.name} value={instructor.id} selected={instructor.name===def ? "selected": ""}>
              {instructor.name}
            </option>
          ))}
        </select>
      </td>
      <td>{department}</td>
      <td>{courseType}</td>
      <td>{courseDegree}</td>
      <td>{credit}</td>

    </tr>
    

    
    

  );
}

export default CourseTable_Manager;