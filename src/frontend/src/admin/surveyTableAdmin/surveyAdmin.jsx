import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../../components/surveyTable.css";

function SurveyTable_Admin(props) {

    const { surveyID, id, creationDatetime, startingDatetime, deadline, trialCount, courseId, instructorId, courseName,
    courseCode, surveyType, instructorName, status} = props;
        let navigate = useNavigate();

        function canEditSurvey(){
                if((status==="SUBMITTED") && (new Date() >= new Date(deadline))){
                    return false;
                                }
                    
                if((status==="SUBMITTED") && (new Date() >= new Date(startingDatetime))){
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
                <td>{creationDatetime===null ? "" :new Date(creationDatetime).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
                <td>{startingDatetime===null ? "" :new Date(startingDatetime).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
                <td>{deadline===null ? "" :new Date(deadline).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
                {canEditSurvey() ? <td><button className="action-button see-answers-button" onClick={(event) => {event.preventDefault(); navigate(`/editSurveyAdmin/${surveyID}`, {state: {props}});} } style={{cursor: "pointer"}}>EDIT</button></td>
                : 
                <td><button className="action-button see-answers-button" onClick={(event) => {event.preventDefault(); 
                    if(new Date() >= new Date(deadline))    alert("Survey has been already closed for evaluation."); 
                    else alert("Survey has been already opened for evaluation.");}} style={{cursor: "pointer"}}>EDIT</button></td> }

                <td>
                    <button className="action-button see-answers-button" onClick={(event) => {event.preventDefault(); navigate(`/viewSurveyAdmin/${surveyID}`);}} style={{cursor: "pointer"}}>PREVIEW</button>
                </td>
                <td><button className="action-button see-answers-button" onClick={(event) => {event.preventDefault(); navigate(`/viewStatsAdmin/${surveyID}`);}} style={{cursor: "pointer"}}>STATISTICS</button></td>
            </tr>
        )
        
}
export default SurveyTable_Admin;