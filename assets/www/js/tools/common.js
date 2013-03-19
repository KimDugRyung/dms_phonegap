function clearEvent(event){
    if(event.preventDefault){ event.preventDefault();}
    else {event.stop();}
    if(event.stopPropagation) event.stopPropagation();
}

// This should be renewed once we come to localize this application. Right now not supported

var localization_messages = {
    en: {
            delete_button_label: 'Delete',
            change_button_label: 'Change',
            fit_button_label: 'Fit',
            header_message: 'Counting shopable products',
            no_ch_confirm_message: 'All pins will be delete. Continue?...'
            //TO DO - add all ids(next version)
        }
};
