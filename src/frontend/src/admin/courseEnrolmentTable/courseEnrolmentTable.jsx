import React from "react";

function CourseEnrolment_Table(props) {

  const {
    courseType,
    courseCode,
    courseName,
    studentName,
    studentSurname,
    studentemail,
    requestTime,
    requestId,
    onSave
  } = props;


  const handleApprove = () => {
    onSave({
      enrolment: {
        courseName: courseName,
        requestId: requestId,
        approveClicked: true,
        denyClicked: false               
    }
  });
}

const handleDeny = () => {

  onSave({
    enrolment: {
      courseName: courseName,
      requestId: requestId,
      approveClicked: false,
      denyClicked: true               
  }
});
}



  return (
    <tr>
      <td>{courseType}</td>
      <td>{courseCode}</td>
      <td>{courseName}</td>
      <td>{studentName}</td>
      <td>{studentSurname}</td>
      <td>{studentemail}</td>
      <td>{requestTime}</td>
      <td>
        <button className="action-button approve-button" onClick={handleApprove}>APPROVE</button>
      </td>
      <td>
        <button className="action-button deny-button" onClick={handleDeny}>DENY</button>
      </td>
    </tr>
  );
}

export default CourseEnrolment_Table;