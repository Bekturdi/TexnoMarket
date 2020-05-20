$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    createUser: '/create-user'
    loginUser: '/login-user'
    addPhone: '/add-phone'
    getPhone: '/get-phone'
    getUser: '/get-user'

  Page =
    card: 'card'
    phone: 'phone'
    computer: 'computer'
    phoneDash: 'phoneDash'

  defaultUserData =
    username: ''
    password: ''
    email: ''

  defaultPhoneData =
    phoneName: ''
    phoneModel: ''
    phoneRam: ''
    phoneHdd: ''
    phonePrice: ''

  vm = ko.mapping.fromJS
    page: Glob.page
    user: defaultUserData
    phone: defaultPhoneData
    getPhoneList: []
    getUserList: []

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
        window.location.href = '/login-page'

  vm.loginUser = ->
    toastr.clear()
    if (!vm.user.username())
      toastr.error("Foydalanuvchi nomini kiriting!")
      return no
    else if (!vm.user.password())
      toastr.error("Passwordni kiriting!")
      return no
    else
      data =
        username: vm.user.username()
        password: vm.user.password()
      $.ajax
        url: apiUrl.loginUser
        type: 'POST'
        data: JSON.stringify(data)
        dataType: 'json'
        contentType: 'application/json'
      .fail handleError
      .done (response) ->
        toastr.success(response)
        window.location.href = '/admin-page'

  vm.addPhone = ->
    toastr.clear()
    if (!vm.phone.phoneName())
      toastr.error("telefon nomini kiriting!")
      return no
    else if (!vm.phone.phoneModel())
      toastr.error("telefon modelini kiriting! \n Masalan Samsung, Apple, Huawei")
      return no
    else if (!vm.phone.phoneRam())
      toastr.error("telefon tezkor xotirasini kiriting!")
      return no
    else if (!vm.phone.phoneHdd())
      toastr.error("telefon doimiy xotirasini kiriting!")
      return no
    else if (!vm.phone.phonePrice())
      toastr.error("telefon bahosini kiriting!")
      return no
    else
      data =
        phoneName: vm.phone.phoneName()
        phoneModel: vm.phone.phoneModel()
        phoneRam: vm.phone.phoneRam()
        phoneHdd: vm.phone.phoneHdd()
        phonePrice: vm.phone.phonePrice()
      $.ajax
        url: apiUrl.addPhone
        type: 'POST'
        data: JSON.stringify(data)
        dataType: 'json'
        contentType: 'application/json'
      .fail handleError
      .done (response) ->
        toastr.success(response)
        ko.mapping.fromJS(defaultPhoneData, {}, vm.phone)
        $("#addEmployeeModal").modal("hide")

  getPhoneList = ->
    $.ajax
      url: apiUrl.getPhone
      type: "GET"
    .fail handleError
    .done (response) ->
       vm.getPhoneList(response)

  getPhoneList()

  getUserList = ->
    $.ajax
      url: apiUrl.getUser
      type: "GET"
    .fail handleError
    .done (response) ->
      vm.getUserList(response)

  getUserList()

  ko.applyBindings {vm}