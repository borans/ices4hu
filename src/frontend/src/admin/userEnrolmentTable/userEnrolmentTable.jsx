import React from "react";

function UserEnrolment_Table(props) {

  const {
    accountType,
    name,
    surname,
    email,
    department,
    requestTime,
    requestId,
    onSave
  } = props;


  const handleApprove = () => {
    onSave({
      enrolment: {
        accountType: accountType,
        requestId: requestId,
        approveClicked: true,
        denyClicked: false               
    }
  });
}

const handleDeny = () => {

  onSave({
    enrolment: {
      accountType: accountType,
      requestId: requestId,
      approveClicked: false,
      denyClicked: true               
  }
});
}

  return (
    <tr>
      <td>{accountType}</td>
      <td>{name}</td>
      <td>{surname}</td>
      <td>{email}</td>
      <td>{department}</td>
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

export default UserEnrolment_Table;