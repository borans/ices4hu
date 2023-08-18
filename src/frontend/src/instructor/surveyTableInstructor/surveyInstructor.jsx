import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../../components/surveyTable.css";
function SurveyTable_Instructor(props) {

    const { surveyID, surveyType, courseCode, courseName ,startingDatetime, deadline, 
        editCount, status, completedPercent, courseID, func, canSeeStats   } = props;
        let navigate = useNavigate();

        function isTimeOk() {
                if (deadline!==null)
                  if(new Date() >= new Date(deadline)) {
                  return false;
                }
              return true;
          }


          function deleteSurvey(){
            console.log(props)
            fetch("/api/survey/instructor?survey="+surveyID+"&user=" + localStorage.getItem("loginID"), {
                method: "DELETE",
                headers: {
                  "Content-Type": "application/json",
                  "Authorization": localStorage.getItem("authToken")
                },
              })
              .then((res) => {
                if (res.ok) {
                    return {json: res.json(),
                        success: true};
                }
                else {
                    return {json: res.json(),
                        success: false};
                }
              })
              .then((result) => {
                if (result.success) {
                    func(surveyID);
                    result.json.then((json) => {
                    alert(json.message);
                })
                } else {
                    result.json.then((json) => {
                        alert(json.message);
                    })
                }
              })
              .catch((err) => {
                console.log("Error:", err);
                alert("COULD NOT DELETE THE SURVEY")
              });
            }

        return(
            <tr>
                <td>{surveyType}</td>
                <td>{courseCode}</td>
                <td>{courseName}</td>
                <td>{startingDatetime===null ? "" :new Date(startingDatetime).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
                <td>{deadline===null ? "" :new Date(deadline).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
                <td>{editCount}/4</td>
                <td>{status}</td>
                {isTimeOk() ? <td><button class="openSurvey" onClick={(event) => {event.preventDefault();navigate(`/editSurvey/${surveyID}`, {state: {surveyID, surveyType, courseCode, courseName ,startingDatetime, deadline, 
        editCount, status, completedPercent, courseID}})}} style={{cursor: "pointer"}}>EDIT</button></td> : <td><button className="openSurvey" onClick={(event) => {event.preventDefault();alert("The deadline is past.")}}style={{cursor: "pointer"}}>EDIT</button></td> }

                <td><button className="openSurvey" onClick={(event) => {event.preventDefault();navigate(`/viewSurveyInstructor/${surveyID}`)}} style={{cursor: "pointer"}}>PREVIEW</button></td>

                <td>{parseFloat(completedPercent).toFixed(2)} </td>
                
                {canSeeStats ?
                <td> <button className="openSurvey" style={{cursor: "pointer"}} onClick={ (event) => {event.preventDefault();navigate(`/viewStatsInstructor/${surveyID}`);}}>STATISTICS</button></td>
                : <td> <button className="openSurvey" style={{cursor: "pointer"}} onClick={ (event) => {event.preventDefault(); alert("You Cannot View Stats Yet.")}}>STATISTICS</button></td>
                }
                <td> <button className="openSurvey" onClick={(event) => {event.preventDefault();deleteSurvey();}} style={{cursor: "pointer"}}>DELETE SURVEY</button></td>

            </tr>
        )
        
}
export default SurveyTable_Instructor;