<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password - Education Stream Advisor</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        
        .container {
            background: white;
            border-radius: 16px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            padding: 40px;
            width: 100%;
            max-width: 420px;
        }
        
        .logo {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .logo-icon {
            width: 60px;
            height: 60px;
            background: linear-gradient(135deg, #2563EB, #7c3aed);
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 15px;
        }
        
        .logo-icon svg {
            width: 32px;
            height: 32px;
            fill: white;
        }
        
        h1 {
            color: #1e293b;
            font-size: 24px;
            font-weight: 700;
            text-align: center;
            margin-bottom: 8px;
        }
        
        .subtitle {
            color: #64748b;
            text-align: center;
            font-size: 14px;
            margin-bottom: 30px;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        label {
            display: block;
            color: #374151;
            font-size: 14px;
            font-weight: 600;
            margin-bottom: 8px;
        }
        
        input[type="password"] {
            width: 100%;
            padding: 14px 16px;
            border: 2px solid #e5e7eb;
            border-radius: 10px;
            font-size: 16px;
            transition: all 0.3s ease;
            outline: none;
        }
        
        input[type="password"]:focus {
            border-color: #2563EB;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
        }
        
        .btn {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #2563EB, #1d4ed8);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .btn:hover {
            background: linear-gradient(135deg, #1d4ed8, #1e40af);
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(37, 99, 235, 0.3);
        }
        
        .btn:disabled {
            background: #9ca3af;
            cursor: not-allowed;
            transform: none;
            box-shadow: none;
        }
        
        .message {
            padding: 12px 16px;
            border-radius: 10px;
            margin-bottom: 20px;
            font-size: 14px;
            display: none;
        }
        
        .message.success {
            background-color: #d1fae5;
            color: #065f46;
            border: 1px solid #6ee7b7;
            display: block;
        }
        
        .message.error {
            background-color: #fee2e2;
            color: #991b1b;
            border: 1px solid #fca5a5;
            display: block;
        }
        
        .back-link {
            text-align: center;
            margin-top: 20px;
        }
        
        .back-link a {
            color: #2563EB;
            text-decoration: none;
            font-size: 14px;
        }
        
        .back-link a:hover {
            text-decoration: underline;
        }
        
        .requirements {
            font-size: 12px;
            color: #6b7280;
            margin-top: 6px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="logo">
            <div class="logo-icon">
                <svg viewBox="0 0 24 24">
                    <path d="M12 3L1 9l4 2.18v6L12 21l7-3.82v-6l2-1.09V17h2V9L12 3zm6.82 6L12 12.72 5.18 9 12 5.28 18.82 9zM17 15.99l-5 2.73-5-2.73v-3.72L12 15l5-2.73v3.72z"/>
                </svg>
            </div>
            <h1>Reset Your Password</h1>
            <p class="subtitle">Enter your new password below</p>
        </div>
        
        <div id="message" class="message"></div>
        
        <form id="resetForm" onsubmit="return handleSubmit(event)">
            <input type="hidden" id="token" name="token" value="<?php echo htmlspecialchars($_GET['token'] ?? ''); ?>">
            
            <div class="form-group">
                <label for="new_password">New Password</label>
                <input type="password" id="new_password" name="new_password" 
                       placeholder="Enter new password" required minlength="6">
                <p class="requirements">Must be at least 6 characters</p>
            </div>
            
            <div class="form-group">
                <label for="confirm_password">Confirm Password</label>
                <input type="password" id="confirm_password" name="confirm_password" 
                       placeholder="Confirm new password" required minlength="6">
            </div>
            
            <button type="submit" class="btn" id="submitBtn">Reset Password</button>
        </form>
        
        <div class="back-link">
            <a href="#">← Back to App</a>
        </div>
    </div>
    
    <script>
        function handleSubmit(event) {
            event.preventDefault();
            
            const token = document.getElementById('token').value;
            const newPassword = document.getElementById('new_password').value;
            const confirmPassword = document.getElementById('confirm_password').value;
            const submitBtn = document.getElementById('submitBtn');
            const messageDiv = document.getElementById('message');
            
            // Validate
            if (!token) {
                showMessage('Invalid reset link. Please request a new password reset.', 'error');
                return false;
            }
            
            if (newPassword.length < 6) {
                showMessage('Password must be at least 6 characters.', 'error');
                return false;
            }
            
            if (newPassword !== confirmPassword) {
                showMessage('Passwords do not match.', 'error');
                return false;
            }
            
            // Disable button
            submitBtn.disabled = true;
            submitBtn.textContent = 'Resetting...';
            
            // Send request
            fetch('reset_password.php', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    token: token,
                    new_password: newPassword,
                    confirm_password: confirmPassword
                })
            })
            .then(response => response.json())
            .then(data => {
                if (data.status) {
                    showMessage(data.message, 'success');
                    document.getElementById('resetForm').style.display = 'none';
                    document.querySelector('.back-link').innerHTML = 
                        '<p style="color: #065f46; font-weight: 600;">✓ You can now login with your new password in the app!</p>';
                } else {
                    showMessage(data.message, 'error');
                    submitBtn.disabled = false;
                    submitBtn.textContent = 'Reset Password';
                }
            })
            .catch(error => {
                showMessage('Something went wrong. Please try again.', 'error');
                submitBtn.disabled = false;
                submitBtn.textContent = 'Reset Password';
            });
            
            return false;
        }
        
        function showMessage(text, type) {
            const messageDiv = document.getElementById('message');
            messageDiv.textContent = text;
            messageDiv.className = 'message ' + type;
        }
        
        // Check if token exists
        window.onload = function() {
            const token = document.getElementById('token').value;
            if (!token) {
                showMessage('Invalid reset link. Please request a new password reset from the app.', 'error');
                document.getElementById('resetForm').style.display = 'none';
            }
        };
    </script>
</body>
</html>
