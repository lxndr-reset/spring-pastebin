<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="en">
<head>
</head>
<body class="hold-transition register-page">
<div class="register-box">
    <div class="card">
        <div class="card-body register-card-body">
            <p class="login-box-msg">Register a new account</p>

            <form action="#" th:action="@{/register}" th:object="${userData}" method="post">
                <div class="alert alert-danger" th:if="${#fields.hasErrors('*')}">
                    <p th:each="err : ${#fields.errors('*')}" th:text="${err}"></p>
                </div>
                <div class="input-group mb-3">
                    <input type="text" class="form-control"  th:field="*{firstName}" placeholder="First Name" th:errorclass="is-invalid">
                    <div class="input-group-append">
                        <div class="input-group-text">
                            <span class="fas fa-user"></span>
                        </div>
                    </div>
                </div>
                <div class="input-group mb-3">
                    <input type="text" class="form-control"  th:field="*{lastName}" placeholder="Last Name" th:errorclass="is-invalid">

                    <div class="input-group-append">
                        <div class="input-group-text">
                            <span class="fas fa-user"></span>
                        </div>
                    </div>
                </div>
                <div class="input-group mb-3">
                    <input type="email" class="form-control" th:field="*{email}" placeholder="Email" th:errorclass="is-invalid">

                    <div class="input-group-append">
                        <div class="input-group-text">
                            <span class="fas fa-envelope"></span>
                        </div>
                    </div>
                </div>
                <div class="input-group mb-3">
                    <input type="password" class="form-control" th:field="*{password}" placeholder="Password" th:errorclass="is-invalid">

                    <div class="input-group-append">
                        <div class="input-group-text">
                            <span class="fas fa-lock"></span>
                        </div>
                    </div>
                </div>
                <div class="input-group mb-3">
                    <input type="password" class="form-control" placeholder="Retype password">
                    <div class="input-group-append">
                        <div class="input-group-text">
                            <span class="fas fa-lock"></span>
                        </div>
                    </div>
                </div>
        </div>
        <!-- /.form-box -->
    </div><!-- /.card -->
</div>
</body>
</html>