// ==================
// = CORE FUNCTIONS =
// ==================
$(document).ready(function() {
    $('.list-group-item').click(function(){
        if($(this).hasClass('active')){
            $('.list-group-item').removeClass('active');
            $('.action-buttons').addClass('hide');
        } else {
            $('.list-group-item').removeClass('active');
            $(this).addClass('active');
            $('.action-buttons').removeClass('hide');
        }
    });

    $('#fullpage').fullpage({
        sectionsColor: ['#fff', '#4BBFC3', '#7BAABE', 'whitesmoke', '#ccddff']
    });
});