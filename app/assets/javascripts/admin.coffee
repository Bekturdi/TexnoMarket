$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    createUser: '/create-user'
    loginUser: '/login-user'

  Page =
    card: 'card'
    phone: 'phone'
    computer: 'computer'

  vm = ko.mapping.fromJS
    page: Glob.page
    username: ''
    password: ''
    email: ''

  vm.selectedPage = (page) ->
    if (page is Page.phone)
      vm.page(Page.phone)
    else if ( page is Page.computer)
      vm.page(Page.computer)
    else
      vm.page(Page.card)

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText) or error.status is 200
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  vm.createUser = ->
    toastr.clear()
    if (!vm.username())
      toastr.error("Foydalanuvchi nomini kiriting!")
      return no
    else if (!vm.password())
      toastr.error("Passwordni kiriting!")
      return no
    else if (!vm.email())
      toastr.error("Emailni kiriting!")
      return no
    else
      data =
        username: vm.username()
        password: vm.password()
        email: vm.email()
      $.ajax
        url: apiUrl.createUser
        type: 'POST'
        data: JSON.stringify(data)
        dataType: 'json'
        contentType: 'application/json'
      .fail handleError
      .done (response) ->
        toastr.success(response)

  vm.loginUser = ->
    toastr.clear()
    if (!vm.username())
      toastr.error("Foydalanuvchi nomini kiriting!")
      return no
    else if (!vm.password())
      toastr.error("Passwordni kiriting!")
      return no
    else
      data =
        username: vm.username()
        password: vm.password()
      $.ajax
        url: apiUrl.loginUser
        type: 'POST'
        data: JSON.stringify(data)
        dataType: 'json'
        contentType: 'application/json'
      .fail handleError
      .done (response) ->
        toastr.success(response)


  ko.applyBindings {vm}