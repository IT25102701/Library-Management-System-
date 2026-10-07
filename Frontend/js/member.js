// Read the logged-in user saved at login.
const member = session.get();
// Only members may use this page: anyone else is sent to the login page.
if (!member || member.role !== "MEMBER") location.href = "login.html";
// Show the member's name in the sidebar.
document.getElementById("memberName").textContent = member.name;
// Show the member's email in the sidebar.
document.getElementById("memberEmail").textContent = member.email;
// Show the first letter of the name in the round avatar.
document.getElementById("avatar").textContent = member.name.charAt(0).toUpperCase();
// Show "Welcome, <first name>" in the page heading.
document.getElementById("welcome").textContent = `Welcome, ${member.name.split(" ")[0]}`;
// The panel on the right where each view is drawn.
const panel = document.getElementById("panel");
// The view currently shown (starts with the overview).
let current = "overview";
// Helper: builds the HTML for one statistic tile (icon, number, label).
function card(title, value, icon) {
    // Return the tile HTML.
    return `<div class="stat-card"><div class="stat-icon"><i class="fas ${icon}"></i></div><div><strong>${value}</strong><span>${title}</span></div></div>`;
} // end of function card()
// Helper: builds an HTML table from column headings and row HTML.
function table(headers, rows) {
    // Return the table HTML (with a "No records found." row when there are no rows).
    return `<div class="table-card"><div class="table-wrap"><table class="lumina-table"><thead><tr>${headers.map((h) => `<th>${h}</th>`).join("")}</tr></thead><tbody>${rows || `<tr><td colspan="${headers.length}"><div class="empty-state">No records found.</div></td></tr>`}</tbody></table></div></div>`;
} // end of function table()
// One function per dashboard view; switchView() runs the one that is chosen.
const views = {
    // Overview view: activity counts.
    overview: async () => {
        // Load four lists from the server at the same time:
        const [r, b, f, n] = await Promise.all([
            // the member's reservations,
            api.myReservations(),
            // loans,
            api.myBorrowings(),
            // fines,
            api.myFines(),
            // and notifications.
            api.myNotifications(),
        ]); // end of list
        // Draw the heading, four tiles (active reservations, active loans, unpaid fines, unread notifications) and the quick-action buttons (including Borrow a Book).
        panel.innerHTML = `<div class="section-head"><div><h2>Account overview</h2><p>Your current library activity at a glance.</p></div><a class="btn btn-primary" href="browse.html">Browse Books</a></div><div class="stats-grid">${card("Reservations", r.filter((x) => x.status === "ACTIVE").length, "fa-bookmark")}${card("Active Borrowings", b.filter((x) => ["ISSUED", "OVERDUE"].includes(x.status)).length, "fa-book-reader")}${card("Unpaid Fines", f.filter((x) => x.status === "UNPAID").length, "fa-coins")}${card("Notifications", n.filter((x) => !x.readFlag).length, "fa-bell")}</div><div style="margin-top:2rem"><h3>Member services</h3><p style="margin:.5rem 0 1.2rem">Search books, reserve available titles, renew eligible borrowings and communicate with the library.</p><div style="display:flex;gap:.7rem;flex-wrap:wrap"><a class="btn btn-primary" href="browse.html">Search Catalogue</a><button class="btn btn-primary" onclick="openBorrowForm()">Borrow a Book</button><button class="btn btn-outline" onclick="switchView('reservations')">My Reservations</button><button class="btn btn-outline" onclick="switchView('feedback')">Send Feedback</button></div></div>`;
    }, // end of overview()
    // Wishlist view.
    wishlist: async () => {
        // Load the member's wishlist.
        const data = await api.myWishlist();
        // Draw the view:
        panel.innerHTML =
            // the heading with a Browse Books button...
            `<div class="section-head"><div><h2>My Wishlist</h2><p>Books saved for later from the catalogue.</p></div><a class="btn btn-primary" href="browse.html">Browse Books</a></div>` +
            // ...plus a table with...
            table(
                // these column headings...
                ["Book", "Author", "Category", "Saved", "Action"],
                // ...and one row per saved book:
                data
                    // convert each item...
                    .map(
                        // ...using this function...
                        (x) =>
                            // ...into a row with title, author, category, date saved and Borrow/Remove buttons,
                            `<tr><td><strong>${escapeHtml(x.book.title)}</strong></td><td>${escapeHtml(x.book.author?.name || "—")}</td><td>${escapeHtml(x.book.category?.name || "—")}</td><td>${new Date(x.createdAt).toLocaleString()}</td><td><button class="btn btn-primary btn-sm" onclick="borrowFromDashboard(${x.book.id})">Borrow</button><button class="btn btn-danger btn-sm" onclick="removeWishlist(${x.book.id})">Remove</button></td></tr>`,
                    ) // end of call
                    // then join the rows into one HTML string.
                    .join(""),
            ); // end of call
    }, // end of wishlist()
    // Reservations view.
    reservations: async () => {
        // Load the member's reservations.
        const data = await api.myReservations();
        // Draw the view:
        panel.innerHTML =
            // the heading with a Reserve a Book button...
            `<div class="section-head"><div><h2>My Reservations</h2><p>View reservation history and cancel active reservations.</p></div><a class="btn btn-primary" href="browse.html">Reserve a Book</a></div>` +
            // ...plus a table with...
            table(
                // these column headings...
                ["Book", "Reserved", "Expires", "Status", "Action"],
                // ...and one row per reservation:
                data
                    // convert each reservation...
                    .map(
                        // ...using this function...
                        (x) =>
                            // ...into a row with the book, dates, a status badge and (if ACTIVE) Borrow/Cancel buttons,
                            `<tr><td><strong>${escapeHtml(x.book.title)}</strong><br><small>${escapeHtml(x.book.author.name)}</small></td><td>${new Date(x.reservedAt).toLocaleString()}</td><td>${x.expiresAt ? new Date(x.expiresAt).toLocaleString() : "—"}</td><td><span class="badge ${badgeClass(x.status)}">${x.status}</span></td><td>${x.status === "ACTIVE" ? `<button class="btn btn-primary btn-sm" onclick="borrowFromDashboard(${x.book.id})">Borrow</button><button class="btn btn-danger btn-sm" onclick="cancelReservation(${x.id})">Cancel</button>` : "—"}</td></tr>`,
                    ) // end of call
                    // then join the rows into one HTML string.
                    .join(""),
            ); // end of call
    }, // end of reservations()
    // Borrowed books view.
    borrowings: async () => {
        // Load the member's borrowing history.
        const data = await api.myBorrowings();
        // Draw the view:
        panel.innerHTML =
            // the heading with a Borrow a Book button, and an empty area for the borrow form...
            `<div class="section-head"><div><h2>Borrowed Books</h2><p>Borrow books, and track issue dates, due dates, returns and renewals.</p></div><button class="btn btn-primary" id="borrowBtn">Borrow a Book</button></div><div id="borrowArea"></div>` +
            // ...plus a table with...
            table(
                // these column headings...
                ["Book / Copy", "Issue Date", "Due Date", "Return Date", "Status", "Action"],
                // ...and one row per loan:
                data
                    // convert each loan...
                    .map(
                        // ...using this function...
                        (x) =>
                            // ...into a row with the book, barcode, dates, a status badge and (if ISSUED) Renew, plus Delete when it was never renewed,
                            `<tr><td><strong>${escapeHtml(x.bookCopy.book.title)}</strong><br><small>${escapeHtml(x.bookCopy.barcode)}</small></td><td>${x.issueDate}</td><td>${x.dueDate}</td><td>${x.returnDate || "—"}</td><td><span class="badge ${badgeClass(x.status)}">${x.status}</span></td><td>${x.status === "ISSUED" ? `<button class="btn btn-outline btn-sm" onclick="renewBorrow(${x.id})">Renew</button>${!x.renewalCount ? `<button class="btn btn-danger btn-sm" onclick="deleteMyBorrow(${x.id})">Delete</button>` : ""}` : "—"}</td></tr>`,
                    ) // end of call
                    // then join the rows into one HTML string.
                    .join(""),
            ); // end of call
        // The Borrow a Book button opens the borrow form.
        document.getElementById("borrowBtn").onclick = showBorrowForm;
        // If the member came from the Overview's Borrow a Book button...
        if (window.openBorrowNext) {
            // ...clear the request...
            window.openBorrowNext = false;
            // ...and open the form straight away.
            showBorrowForm();
        } // end of if block
    }, // end of borrowings()
    // Fines view.
    fines: async () => {
        // Load the member's fines.
        const data = await api.myFines();
        // Draw the view:
        panel.innerHTML =
            // the heading (explaining that payments are recorded by staff)...
            `<div class="section-head"><div><h2>My Fines</h2><p>Online payment gateway is not included; payment status is recorded by library staff.</p></div></div>` +
            // ...plus a table with...
            table(
                // these column headings...
                ["Fine", "Book", "Reason", "Amount", "Status", "Created"],
                // ...and one row per fine:
                data
                    // convert each fine...
                    .map(
                        // ...using this function...
                        (x) =>
                            // ...into a row with the fine number, book, reason, amount in rupees, status badge and date,
                            `<tr><td>#${x.id}</td><td>${escapeHtml(x.borrowRecord.bookCopy.book.title)}</td><td>${escapeHtml(x.reason)}</td><td><strong>Rs. ${Number(x.amount).toFixed(2)}</strong></td><td><span class="badge ${badgeClass(x.status)}">${x.status}</span></td><td>${new Date(x.createdAt).toLocaleString()}</td></tr>`,
                    ) // end of call
                    // then join the rows into one HTML string.
                    .join(""),
            ); // end of call
    }, // end of fines()
    // Feedback & complaints view.
    feedback: async () => {
        // Load the member's feedback.
        const data = await api.myFeedback();
        // Draw the view:
        panel.innerHTML =
            // the heading with a New Message button and an empty form area...
            `<div class="section-head"><div><h2>Feedback & Complaints</h2><p>Submit feedback or a complaint and track its status.</p></div><button class="btn btn-primary" id="newFeedback">New Message</button></div><div id="feedbackForm"></div>` +
            // ...plus a table with...
            table(
                // these column headings...
                ["Type", "Subject", "Status", "Created"],
                // ...and one row per message:
                data
                    // convert each message...
                    .map(
                        // ...using this function...
                        (x) =>
                            // ...into a row with the type, subject, message, status badge and date,
                            `<tr><td>${x.type}</td><td><strong>${escapeHtml(x.subject)}</strong><br><small>${escapeHtml(x.message)}</small></td><td><span class="badge ${badgeClass(x.status)}">${x.status}</span></td><td>${new Date(x.createdAt).toLocaleString()}</td></tr>`,
                    ) // end of call
                    // then join the rows into one HTML string.
                    .join(""),
            ); // end of call
        // When New Message is clicked:
        document.getElementById("newFeedback").onclick = () => {
            // show the form...
            document.getElementById("feedbackForm").innerHTML =
                // ...with a Type choice (FEEDBACK/COMPLAINT), Subject, Message and a Submit button,
                `<div style="padding:1.3rem;border:1px solid var(--border);border-radius:16px;margin-bottom:1rem"><div class="form-grid"><div class="form-group"><label>Type</label><select id="fbType"><option>FEEDBACK</option><option>COMPLAINT</option></select></div><div class="form-group"><label>Subject</label><input id="fbSubject"></div><div class="form-group full"><label>Message</label><textarea id="fbMessage" rows="4" style="width:100%;background:var(--bg-card);border:1px solid var(--border);border-radius:8px;padding:.8rem;color:white"></textarea></div></div><button class="btn btn-primary" id="sendFeedback">Submit</button></div>`;
            // and make Submit call submitFeedback().
            document.getElementById("sendFeedback").onclick = submitFeedback;
        }; // end of callback
    }, // end of feedback()
    // Profile & password view.
    profile: async () => {
        // Load the member's profile.
        const p = await api.myProfile();
        // Draw the profile form (name, phone, email shown read-only) and the change-password form.
        panel.innerHTML = `<div class="section-head"><div><h2>Profile Management</h2><p>Update your personal information or change your password.</p></div></div><div class="table-card" style="padding:1.4rem"><div class="form-grid"><div class="form-group"><label>Name</label><input id="profileName" value="${escapeHtml(p.name)}"></div><div class="form-group"><label>Phone</label><input id="profilePhone" value="${escapeHtml(p.phone || "")}"></div><div class="form-group full"><label>Email</label><input value="${escapeHtml(p.email)}" disabled></div></div><button class="btn btn-primary" id="saveProfile">Save Profile</button></div><div class="table-card" style="padding:1.4rem;margin-top:1rem"><h3>Change Password</h3><div class="form-grid" style="margin-top:1rem"><div class="form-group"><label>Current Password</label><input type="password" id="currentPassword"></div><div class="form-group"><label>New Password</label><input type="password" id="newPassword" minlength="6"></div></div><button class="btn btn-outline" id="changePassword">Change Password</button></div>`;
        // Save Profile button runs saveProfile().
        document.getElementById("saveProfile").onclick = saveProfile;
        // Change Password button runs changePassword().
        document.getElementById("changePassword").onclick = changePassword;
    }, // end of profile()
    // Notifications view.
    notifications: async () => {
        // Load the member's notifications.
        const data = await api.myNotifications();
        // Draw the view:
        panel.innerHTML =
            // the heading...
            `<div class="section-head"><div><h2>Notifications</h2><p>Reservation, borrowing, due-date and fine alerts.</p></div></div>` +
            // ...then, if there are notifications...
            (data.length
                // ...go through them...
                ? data
                      // ...converting each one...
                      .map(
                          // ...using this function...
                          (x) =>
                              // ...into a card with the title, message, date and (if unread) a Mark read button, unread ones fully opaque,
                              `<div class="review-card" style="opacity:${x.readFlag ? 0.75 : 1}"><div style="display:flex;justify-content:space-between;gap:1rem"><div><strong>${escapeHtml(x.title)}</strong><p style="margin-top:.35rem">${escapeHtml(x.message)}</p><small>${new Date(x.createdAt).toLocaleString()}</small></div>${!x.readFlag ? `<button class="btn btn-outline btn-sm" onclick="markRead(${x.id})">Mark read</button>` : ""}</div></div>`,
                      ) // end of call
                      // and join the cards together;
                      .join("")
                // otherwise show "No notifications yet."
                : '<div class="empty-state">No notifications yet.</div>');
    }, // end of notifications()
}; // end of object
// Draws the current view, showing an error message in the panel if loading fails.
async function safeLoad() {
    // Try:
    try {
        // run the function of the current view and wait for it.
        await views[current]();
    // If it failed (e.g. the server is down)...
    } catch (e) {
        // ...show the error message in the panel.
        panel.innerHTML = `<div class="empty-state">${escapeHtml(e.message)}</div>`;
    } // end of catch block
} // end of function safeLoad()
// switchView(v): shows another view (also used by buttons in the pages).
window.switchView = (v) => {
    // Remember the chosen view.
    current = v;
    // Update the sidebar:
    document
        // for every sidebar link...
        .querySelectorAll(".dash-nav a")
        // ...highlight the one whose data-view matches.
        .forEach((a) => a.classList.toggle("active", a.dataset.view === v));
    // Draw the chosen view.
    safeLoad();
}; // end of switchView()
// For every sidebar link:
document.querySelectorAll(".dash-nav a").forEach(
    // take the link...
    (a) =>
        // ...and when it is clicked:
        (a.onclick = (e) => {
            // stop the browser following the "#" link,
            e.preventDefault();
            // and show the view named in its data-view attribute.
            switchView(a.dataset.view);
        }), // end of callback
); // end of call
// Remove button in the wishlist: removes one book.
window.removeWishlist = async (bookId) => {
    // Try:
    try {
        // ask the server to remove it,
        await api.removeWishlist(bookId);
        // confirm with a pop-up,
        toast("Removed from wishlist.");
        // and redraw the view.
        safeLoad();
    // If it failed...
    } catch (e) {
        // ...show the error in a red pop-up.
        toast(e.message, true);
    } // end of catch block
}; // end of removeWishlist()
// Cancel button in reservations.
window.cancelReservation = async (id) => {
    // Ask for confirmation first; stop if the member clicks Cancel.
    if (!confirm("Cancel this reservation?")) return;
    // Try:
    try {
        // ask the server to cancel it,
        await api.cancelReservation(id);
        // confirm with a pop-up,
        toast("Reservation cancelled.");
        // and redraw the view.
        safeLoad();
    // If it failed...
    } catch (e) {
        // ...show the error in a red pop-up.
        toast(e.message, true);
    } // end of catch block
}; // end of cancelReservation()
// Renew button in borrowed books.
window.renewBorrow = async (id) => {
    // Try:
    try {
        // ask the server to renew the loan,
        await api.renewMyBorrow(id);
        // confirm with a pop-up,
        toast("Borrowing renewed.");
        // and redraw the view.
        safeLoad();
    // If it failed (e.g. renewal limit reached)...
    } catch (e) {
        // ...show the error in a red pop-up.
        toast(e.message, true);
    } // end of catch block
}; // end of renewBorrow()
// Overview's Borrow a Book button: opens the Borrowed Books view with the borrow form.
window.openBorrowForm = () => {
    // Ask the Borrowed Books view to open the form once it is drawn...
    window.openBorrowNext = true;
    // ...and show that view.
    switchView("borrowings");
}; // end of openBorrowForm()
// Shows the Borrow a Book form: a list of the books that have a copy on the shelf right now.
async function showBorrowForm() {
    // The area under the heading.
    const area = document.getElementById("borrowArea");
    // Show a loading message while the books load.
    area.innerHTML = '<div class="empty-state">Loading available books...</div>';
    // Try:
    try {
        // load the whole catalogue (it includes how many copies are available),
        const books = await api.publicBooks("");
        // keep only the books with at least one available copy,
        const available = books.filter((b) => Number(b.availableCopies) > 0);
        // and draw the form: a book list (title, author, copies available) and a Borrow button.
        area.innerHTML = `<div class="card" style="margin-bottom:1.2rem"><div class="form-grid"><div class="form-group full"><label for="borrowBookId">Book to borrow</label><select id="borrowBookId"><option value="">Select an available book</option>${available.map((b) => `<option value="${b.id}">${escapeHtml(b.title)} - ${escapeHtml(b.author?.name || "Unknown")} (${b.availableCopies} available)</option>`).join("")}</select></div></div><button class="btn btn-primary" id="borrowNow">Borrow</button></div>`;
        // The Borrow button borrows the chosen book (validation.js checks a book was chosen first).
        document.getElementById("borrowNow").onclick = () => borrowFromDashboard(Number(document.getElementById("borrowBookId").value));
    // If the books could not be loaded...
    } catch (e) {
        // ...show the error in the area.
        area.innerHTML = `<div class="empty-state">${escapeHtml(e.message)}</div>`;
    } // end of catch block
} // end of function showBorrowForm()
// Borrow buttons in the dashboard (borrow form, reservations, wishlist): borrows one book online.
window.borrowFromDashboard = async (bookId) => {
    // Ask for confirmation first; stop if the member cancels.
    if (!confirm("Borrow this book now? A copy will be set aside for you to collect at the library desk.")) return;
    // Try:
    try {
        // ask the server to create the loan (it picks an available copy and checks the limits),
        const loan = await api.borrowBook(bookId);
        // confirm with a pop-up showing the due date,
        toast(`Borrowed! Please collect your copy at the desk. Due on ${loan.dueDate}.`);
        // and redraw the current view (the new loan / changed reservation appears).
        safeLoad();
    // If the server refused (e.g. borrow limit reached or no copy available)...
    } catch (e) {
        // ...show its message in a red pop-up.
        toast(e.message, true);
    } // end of catch block
}; // end of borrowFromDashboard()
// Delete button in Borrowed Books: the member deletes one of their own borrowings.
window.deleteMyBorrow = async (id) => {
    // Ask for confirmation first; stop if the member cancels.
    if (!confirm("Delete this borrowing? The copy will go back on the shelf for other members.")) return;
    // Try:
    try {
        // ask the server to delete it (it checks the borrowing is yours, still ISSUED, never renewed and without a fine),
        await api.deleteMyBorrow(id);
        // confirm with a pop-up,
        toast("Borrowing deleted. The copy is back on the shelf.");
        // and redraw the view.
        safeLoad();
    // If the server refused...
    } catch (e) {
        // ...show its reason in a red pop-up.
        toast(e.message, true);
    } // end of catch block
}; // end of deleteMyBorrow()
// Submit button of the feedback form.
window.submitFeedback = async () => {
    // Try:
    try {
        // send the feedback to the server with:
        await api.submitFeedback({
            // the chosen type,
            type: document.getElementById("fbType").value,
            // the subject without surrounding spaces,
            subject: document.getElementById("fbSubject").value.trim(),
            // and the message without surrounding spaces;
            message: document.getElementById("fbMessage").value.trim(),
        }); // end of object
        // confirm with a pop-up,
        toast("Message submitted.");
        // and redraw the view.
        safeLoad();
    // If it failed...
    } catch (e) {
        // ...show the error in a red pop-up.
        toast(e.message, true);
    } // end of catch block
}; // end of submitFeedback()
// Save Profile button.
window.saveProfile = async () => {
    // Try:
    try {
        // send the new details to the server:
        const updated = await api.updateProfile({
            // the name without surrounding spaces,
            name: document.getElementById("profileName").value.trim(),
            // and the phone without surrounding spaces;
            phone: document.getElementById("profilePhone").value.trim(),
        }); // end of object
        // save the updated user in the browser,
        session.set(updated);
        // update the name in the sidebar,
        document.getElementById("memberName").textContent = updated.name;
        // and confirm with a pop-up.
        toast("Profile updated.");
    // If it failed...
    } catch (e) {
        // ...show the error in a red pop-up.
        toast(e.message, true);
    } // end of catch block
}; // end of saveProfile()
// Change Password button.
window.changePassword = async () => {
    // Try:
    try {
        // send to the server:
        await api.changePassword({
            // the current password,
            currentPassword: document.getElementById("currentPassword").value,
            // and the new password;
            newPassword: document.getElementById("newPassword").value,
        }); // end of object
        // confirm with a pop-up,
        toast("Password changed successfully.");
        // clear the current-password box,
        document.getElementById("currentPassword").value = "";
        // and clear the new-password box.
        document.getElementById("newPassword").value = "";
    // If it failed (e.g. wrong current password)...
    } catch (e) {
        // ...show the error in a red pop-up.
        toast(e.message, true);
    } // end of catch block
}; // end of changePassword()
// Mark read button on a notification.
window.markRead = async (id) => {
    // Try:
    try {
        // tell the server it was read,
        await api.readNotification(id);
        // and redraw the view.
        safeLoad();
    // If it failed...
    } catch (e) {
        // ...show the error in a red pop-up.
        toast(e.message, true);
    } // end of catch block
}; // end of markRead()
// Draw the first view (the overview) when the page opens.
safeLoad();
