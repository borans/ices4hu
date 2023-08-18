import React from "react";
import "./dmSurveyTableStyle.css";
import { useNavigate } from "react-router-dom";
function SurveyTable_Manager(props) {
  let navigate = useNavigate();
  const {
    surveyID,
    creationDateTime,
    startingDatetime,
    deadline,
    trialCount,
    courseId,
    instructorId,
    courseName,
    courseCode,
    surveyType,
    instructorName,
    status,
    openForReevaluation,
    onSave
  } = props;

  
  const handleApprove = () => {
    onSave({
      reevaluation: {
          surveyID: surveyID,
          openForReevaluation: openForReevaluation,
          approveClicked: true,
          denyClicked: false        
    }
  });
}

const handleDeny = () => {

  onSave({
    reevaluation: {
        surveyID: surveyID,
        openForReevaluation: openForReevaluation,
        approveClicked: false,
        denyClicked: true        
  }
});


}


  return (
    <tr>
      <td>{surveyType}</td>
      <td>{courseCode}</td>
      <td>{courseName}</td>
      <td>{instructorName}</td>
      <td>{startingDatetime===null ? "" :new Date(startingDatetime).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
      <td>{deadline===null ? "" :new Date(deadline).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
      <td>{openForReevaluation===true ? "EXISTS":"DOES NOT EXIST"}</td>
      <td>
        <button className="action-button approve-button" onClick={handleApprove}>APPROVE</button>
      </td>
      <td>
        <button className="action-button deny-button" onClick={handleDeny}>DENY</button>
      </td>
      <td>
        <button className="action-button statistics-button" onClick={(event) => {event.preventDefault(); navigate(`/viewStatsDM/${surveyID}`);}}>STATISTICS</button>
      </td>
      <td>
        <button className="action-button see-answers-button" onClick={(event) => {event.preventDefault(); navigate(`/viewSurveyDM/${surveyID}`);}} style={{cursor: "pointer"}}>PREVIEW</button>
      </td>
      <td>
        <button className="action-button see-answers-button" onClick={(event) => {event.preventDefault(); navigate(`/surveySubmitters/${surveyID}`, {state: {props}});}} style={{cursor: "pointer"}}>SEE ANSWERS</button>
      </td>
    </tr>
  );
}

export default SurveyTable_Manager;
