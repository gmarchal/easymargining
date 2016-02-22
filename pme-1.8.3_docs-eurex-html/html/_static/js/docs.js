$(document).ready(function() {
  $('div.important > p.admonition-title').addClass('fa fa-info-circle');
  $('div.note > p.admonition-title').addClass('fa fa-check-circle');
  $('div.warning > p.admonition-title').addClass('fa fa-exclamation-triangle');
  $('div.important > p.admonition-title').text(function(i,original) {
    return " "+original
  });
  $('div.note > p.admonition-title').text(function(i,original) {
    return " "+original
  });
  $('div.warning > p.admonition-title').text(function(i,original) {
    return " "+original
  });
});
