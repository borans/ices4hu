import React from 'react';
import SideNav, {Toggle, NavItem, NavIcon, NavText} from '@trendmicro/react-sidenav';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faHome, faBook, faSquarePollVertical, faCalendar, faRegistered } from '@fortawesome/free-solid-svg-icons'

import '@trendmicro/react-sidenav/dist/react-sidenav.css';
import '../../components/sidebar.css'

import { useNavigate } from 'react-router-dom';

function SideBarStudent() {
    const navigate = useNavigate();
    return <SideNav
        onSelect={selected=> {
            navigate('/' + selected);
        }}
        className='sidebar'
        >
            <SideNav.Toggle />
            <SideNav.Nav defaultSelected="home">
                <NavItem eventKey="mainPageStudent">
                    <NavIcon><FontAwesomeIcon icon={faHome} /></NavIcon>
                    <NavText>Home</NavText>
                </NavItem>

                <NavItem eventKey="studentCourses">
                    <NavIcon><FontAwesomeIcon icon={faBook} /></NavIcon>
                    <NavText>My Courses</NavText>
                </NavItem>

                <NavItem eventKey="studentCourseReg">
                    <NavIcon><FontAwesomeIcon icon={faRegistered} /></NavIcon>
                    <NavText>Course Registration</NavText>
                </NavItem>

                <NavItem eventKey="studentSurveys">
                    <NavIcon><FontAwesomeIcon icon={faSquarePollVertical} /></NavIcon>
                    <NavText>My Surveys</NavText>
                </NavItem>

                <NavItem eventKey="studentSchedule">
                    <NavIcon><FontAwesomeIcon icon={faCalendar} /></NavIcon>
                    <NavText>Schedule</NavText>
                </NavItem>
                
            </SideNav.Nav>
        </SideNav>
};

export default SideBarStudent;