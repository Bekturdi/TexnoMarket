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
    phoneDash: 'phoneDash'

  defaultUserData =
    username: ''
    password: ''
    email: ''

  vm = ko.mapping.fromJS
    page: Glob.page
    user: defaultUserData

  vm.selectedPage = (page) ->
    if (page is Page.phone)
      vm.page(Page.phone)
    else if ( page is Page.computer)
      vm.page(Page.computer)
    else if ( page is Page.phoneDash)
      vm.page(Page.phoneDash)
    else
      vm.page(Page.card)

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText) or error.status is 200
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  vm.createUser = ->
    toastr.clear()
    if (!vm.user.username())
      toastr.error("Foydalanuvchi nomini kiriting!")
      return no
    else if (!vm.user.password())
      toastr.error("Passwordni kiriting!")
      return no
    else if (!vm.user.email())
      toastr.error("Emailni kiriting!")
      return no
    else
      data =
        username: vm.user.username()
        password: vm.user.password()
        email: vm.user.email()
      $.ajax
        url: apiUrl.createUser
        type: 'POST'
        data: JSON.stringify(data)
        dataType: 'json'
        contentType: 'application/json'
      .fail handleError
      .done (response) ->
        toastr.success(response)
        ko.mapping.fromJS(defaultUserData, {}, vm.user)
        window.location.href = '/admin-page'

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