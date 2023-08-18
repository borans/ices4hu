import React from 'react';
import SideNav, {Toggle, NavItem, NavIcon, NavText} from '@trendmicro/react-sidenav';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faHome, faBook, faSquarePollVertical, faNewspaper, faCalendar, faQuestion } from '@fortawesome/free-solid-svg-icons'

import '@trendmicro/react-sidenav/dist/react-sidenav.css';
import "../../components/sidebar.css";

import { useNavigate } from 'react-router-dom';

function SideBarInstructor() {
    const navigate = useNavigate();
    return <SideNav
        onSelect={selected=> {
            navigate('/' + selected);
        }}
        className='sidebar'
        >
            <SideNav.Toggle />
            <SideNav.Nav defaultSelected="home">
                <NavItem eventKey="mainPageInstructor">
                    <NavIcon><FontAwesomeIcon icon={faHome} /></NavIcon>
                    <NavText>Home</NavText>
                </NavItem>

                <NavItem eventKey="instructorCourses">
                    <NavIcon><FontAwesomeIcon icon={faBook} /></NavIcon>
                    <NavText>My Courses</NavText>
                </NavItem>

                <NavItem eventKey="instructorSurveys">
                    <NavIcon><FontAwesomeIcon icon={faSquarePollVertical} /></NavIcon>
                    <NavText>My Surveys</NavText>
                </NavItem>

                <NavItem eventKey="instructorQB">
                    <NavIcon><FontAwesomeIcon icon={faQuestion} /></NavIcon>
                    <NavText>Question Bank</NavText>
                </NavItem>

                <NavItem eventKey="instructorNR">
                    <NavIcon><FontAwesomeIcon icon={faNewspaper} /></NavIcon>
                    <NavText>Department Newsletter</NavText>
                </NavItem>

                <NavItem eventKey="instructorSchedule">
                    <NavIcon><FontAwesomeIcon icon={faCalendar} /></NavIcon>
                    <NavText>Schedule</NavText>
                </NavItem>
                
            </SideNav.Nav>
        </SideNav>
};

export default SideBarInstructor;