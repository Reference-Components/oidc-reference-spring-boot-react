import React from "react";
import axios from "axios";

const backendBaseUrl = 'http://localhost:8080';

class App extends React.Component {

  constructor(props) {
    super(props);
    this.state = {}
    this.logout = this.logout.bind(this);
  }

  componentDidMount() {
    let jwt;
    if (document.cookie.indexOf('jwt-token=') !== -1) {
      jwt = document.cookie
          .split('; ')
          .find(row => row.startsWith('jwt-token='))
          .split('=')[1];
    } else {
      jwt = '';
    }

    axios({
      method: 'get',
      url: backendBaseUrl + '/current-user',
      withCredentials: true,
      headers: {
        'Authorization': 'Bearer ' + jwt
      }
    }).then(response => {
      this.setState({
        user: response.data
      })
    }).catch(error => {
      console.log('GETTING USER FAILED', error)
      alert('Getting current user failed, check console for details')
    })
  }

  render() {
    if (this.state.user) {
      console.log(this.state.user);
      let name = this.state.user.fullName;
      if (!name) {
        name = this.state.user.name;
      }
      return (
          <div>
            <p>Current authenticated user: {name}</p>
            <p>Whole authentication object is logged in console for reference.</p>
            <input type="button" value="Log out" onClick={this.logout}/>
          </div>
      )
    } else {
      return (
          <div>
            <p>Not logged in.</p>
            <a className="button" href={backendBaseUrl + '/oauth2/authorization/google'}>Log in with Google</a>
            <a className="button" href={backendBaseUrl + '/oauth2/authorization/ms_single'}>Log in with single tenant Microsoft account</a>
            <a className="button" href={backendBaseUrl + '/oauth2/authorization/ms_personal'}>Log in with personal Microsoft account</a>
            <a className="button" href={backendBaseUrl + '/oauth2/authorization/ms_multitenant'}>Log in with any Microsoft account</a>
          </div>
      )
    }
  }

  logout() {
    axios({
      method: 'post',
      url: backendBaseUrl + '/logout',
      withCredentials: true
    }).then(() => {
      window.location = '/'
    }).catch(error => {
      console.log('LOG OUT FAILED', error)
      alert('Log out failed, check console for details')
    });
  }
}

export default App;
