import React from "react";
import"./surveyQuestion.css";

function SurveyQuestion(props) {
  const {content, isMultipleChoice, multipleChoices} = props;


  return (
    <div className="survey-question">
      <div><label>{content}</label>
        </div>
      
      {isMultipleChoice ? (
        multipleChoices
        .sort((a, b) => a.id - b.id)
          .map((choice) => (
            <label key={choice.id}>
              <input
                type="radio"
                disabled ={true}
              />
              {choice.content}
            </label>
          ))
      ) : (
        <input
          type="text"
          readOnly={true}
          placeholder={"(open-ended question)"}
        />
      )}
    </div>
  ); 
}

export default SurveyQuestion;