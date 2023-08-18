import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../../components/surveyTable.css";
function SurveyTable_Student(props) {

    const { surveyID, surveyType, courseCode, courseName, instructorName,
        startingDatetime, deadline, editCount, status, daysUntilDeadline } = props;
        let navigate = useNavigate();

        function isTimeOk() {
            
            const startingDateTime = props.startingDatetime.replace(' ', 'T');
            const startingDate = new Date(Date.parse(startingDateTime));
            const currentDate = new Date();
                if (currentDate.getTime() <= startingDate.getTime()) {
                  return false;
                }
              return true;
            
          }


        return(
            <tr>
                <td>{courseCode}</td>
                <td>{courseName}</td>
                <td>{surveyType}</td>
                <td>{instructorName}</td>
                <td>{startingDatetime===null ? "" :new Date(startingDatetime).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
                <td>{deadline===null ? "" :new Date(deadline).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
                <td>{editCount}/3</td>
                {isTimeOk() ? <td><button class="openSurvey" onClick={(event) => {event.preventDefault(); navigate(`/survey/${surveyID}`, {state: props})}} style={{cursor: "pointer"}}>OPEN</button></td> :<td><button class="openSurvey" onClick={(event) => {event.preventDefault(); alert("FORM IS NOT OPENED YET")}}style={{cursor: "pointer"}}>OPEN</button></td>}
                <td>{status}</td>
                <td>{daysUntilDeadline}</td>
            </tr>
        )
        
}
export default SurveyTable_Student;