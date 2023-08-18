import {useLocation, useParams} from "react-router-dom";
import React, {useEffect, useState} from "react";
import TopBar from "../topbarManager/topbarManager";
import SideBar from "../sidebarManager/sidebarManager";
import StatsQuestion from "../../components/statsQuestion/statsQuestion";

function ViewStatsDepartmentManager(props){

    const { surveyID } = useParams();
    const [questions, setQuestions] = useState([]);
    const { state } = useLocation();
    const [QB, setQB] = useState([]);
    let questionID;

    useEffect(() => {
        fetchQB();
    }, [])




    const fetchQB = () => {
        console.log(localStorage.getItem("surveyID"));
        fetch("/api/survey/statistics?survey=" + surveyID, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": localStorage.getItem("authToken")
            },
        })
            .then((res) => {
                    if (res.ok)
                        return res.json();
                    else
                        throw new Error("AN ISSUE CONNECTING TO BACKEND");
                }
            )
            .then(result => {
                console.log(result);
                setQuestions(result);
                console.log(questions);
            })
            .catch((err) => {
                console.log("Error:", err);
            });
    };

    return (
        <div>
            <TopBar />
            <div>
                <div>
                    <SideBar />
                </div>
                <div className="container">
                    <h1 className="page-title"> Results</h1>
                    <div className="createBox">
                        <div className="form-container">
                            <form className="form">
                                <label>QUESTIONS</label>
                                {questions
                                    .map((question) => (
                                        <StatsQuestion
                                            key={question.id}
                                            base64 = {question.base64}
                                            content={question.question}
                                            isMultipleChoice={question.isMultipleChoice}
                                            answers={question.answers}
                                        />
                                    ))}
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
export default ViewStatsDepartmentManager;