import React from 'react';
import SideNav, {Toggle, NavItem, NavIcon, NavText} from '@trendmicro/react-sidenav';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faHome, faBook, faSquarePollVertical, faUsers, faCalendar } from '@fortawesome/free-solid-svg-icons'

import '@trendmicro/react-sidenav/dist/react-sidenav.css';
import '../../components/sidebar.css'

import { useNavigate } from 'react-router-dom';

function SideBarAdmin() {
    const navigate = useNavigate();
    return <SideNav
        onSelect={selected=> {
            navigate('/' + selected);
        }}
        className='sidebar'
        >
            <SideNav.Toggle />
            <SideNav.Nav defaultSelected="home">
                <NavItem eventKey="mainPageAdmin">
                    <NavIcon><FontAwesomeIcon icon={faHome} /></NavIcon>
                    <NavText>Home</NavText>
                </NavItem>

                <NavItem eventKey="adminCourses">
                    <NavIcon><FontAwesomeIcon icon={faBook} /></NavIcon>
                    <NavText>Courses</NavText>
                </NavItem>

                <NavItem eventKey="adminSurveys">
                    <NavIcon><FontAwesomeIcon icon={faSquarePollVertical} /></NavIcon>
                    <NavText>Surveys</NavText>
                </NavItem>

                <NavItem eventKey="viewUserAndAddInstructor">
                    <NavIcon><FontAwesomeIcon icon={faUsers} /></NavIcon>
                    <NavText>Accounts</NavText>
                </NavItem>

                <NavItem eventKey="adminSchedule">
                    <NavIcon><FontAwesomeIcon icon={faCalendar} /></NavIcon>
                    <NavText>Schedule</NavText>
                </NavItem>
                
            </SideNav.Nav>
        </SideNav>
};

export default SideBarAdmin;