import { SvgIcon } from "@material-ui/core";
import { Question } from 'react-bootstrap-icons';


export default function TypeIcon (props) {
    const { type, className } = props;
    switch (type) {
        case 'bal':
            return <SvgIcon className={className} viewBox="0 0 200 200">
                <path fill="#000" d="M92.9 13.4v64.2L52.2 62V13.4zM52.2 98.6V77.2l27.9 10.7zm40.7 23.2-19.8 64.9H52.2v-72.9l40.7-15.6zm14.2-108.4v64.2L147.8 62V13.4zm40.7 85.2V77.2l-27.9 10.7zm-40.7 23.2 19.7 64.9h21v-72.9l-40.7-15.6z" />
            </SvgIcon>
        case 'mi':
            return <SvgIcon className={className} viewBox="-4 -4 24 24">
                <path fill="#000" fill-rule="evenodd" d="M13.75.5a2.25 2.25 0 0 0-1.85 3.54l-.93.93a.75.75 0 0 0-.11.14c-.19-.07-.4-.11-.61-.11h-2.5A1.75 1.75 0 0 0 6 6.75v.5H4.37a2.25 2.25 0 1 0 0 1.5H6v.5c0 .97.78 1.75 1.75 1.75h2.5c.21 0 .42-.04.61-.11l.11.14.93.93a2.25 2.25 0 1 0 1.24-.88l-1.11-1.11a.75.75 0 0 0-.14-.11c.07-.19.11-.4.11-.61v-2.5c0-.21-.04-.42-.11-.61a.75.75 0 0 0 .14-.11l1.11-1.11A2.25 2.25 0 0 0 16 2.75 2.25 2.25 0 0 0 13.75.5zM13 2.75a.75.75 0 1 1 1.5 0 .75.75 0 0 1-1.5 0zM7.75 6.5a.25.25 0 0 0-.25.25v2.5c0 .14.11.25.25.25h2.5a.25.25 0 0 0 .25-.25v-2.5a.25.25 0 0 0-.25-.25h-2.5zm6 6a.75.75 0 1 0 0 1.5.75.75 0 0 0 0-1.5zM1.5 8A.75.75 0 1 1 3 8a.75.75 0 0 1-1.5 0z" />
            </SvgIcon>
        default:
            return <Question className={className}/>
    }
}
