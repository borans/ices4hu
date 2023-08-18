import React from 'react';
import SideNav, {Toggle, NavItem, NavIcon, NavText} from '@trendmicro/react-sidenav';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faHome, faBook, faSquarePollVertical, faCalendar, faNewspaper } from '@fortawesome/free-solid-svg-icons'

import '@trendmicro/react-sidenav/dist/react-sidenav.css';
import '../../components/sidebar.css'

import { useNavigate } from 'react-router-dom';

function SideBarManager() {
    const navigate = useNavigate();
    return <SideNav
        onSelect={selected=> {
            navigate('/' + selected);
        }}
        className='sidebar'
        >
            <SideNav.Toggle />
            <SideNav.Nav defaultSelected="home">
                <NavItem eventKey="mainPageManager">
                    <NavIcon><FontAwesomeIcon icon={faHome} /></NavIcon>
                    <NavText>Home</NavText>
                </NavItem>

                <NavItem eventKey="managerCourses">
                    <NavIcon><FontAwesomeIcon icon={faBook} /></NavIcon>
                    <NavText>Courses</NavText>
                </NavItem>

                <NavItem eventKey="managerSurveys">
                    <NavIcon><FontAwesomeIcon icon={faSquarePollVertical} /></NavIcon>
                    <NavText>Surveys</NavText>
                </NavItem>

                <NavItem eventKey="managerNR">
                    <NavIcon><FontAwesomeIcon icon={faNewspaper} /></NavIcon>
                    <NavText>Department Newsletter</NavText>
                </NavItem>

                <NavItem eventKey="managerSchedule">
                    <NavIcon><FontAwesomeIcon icon={faCalendar} /></NavIcon>
                    <NavText>Schedule</NavText>
                </NavItem>
                
            </SideNav.Nav>
        </SideNav>
};

export default SideBarManager;