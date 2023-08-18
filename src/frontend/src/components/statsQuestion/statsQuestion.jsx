import React, { useState } from "react";


function StatsQuestion(props) {
    const { id, content,base64, isMultipleChoice, answers, onSave } = props;
    const [text, setText] = useState();
    let srcString = "data:image/png;base64," + base64;



    return (
        <div className="survey-question">
            <div><label><b><u>{content}</u></b></label>
                {isMultipleChoice ?
                    <img src={srcString}/>
                :
                    answers.map( (answer) =>

                        <label>"<i>{answer}</i>"</label>

                    )
                }

            </div>


        </div>
    );
}

export default StatsQuestion;